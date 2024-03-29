package it.erweb.crawler.parser;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.erweb.crawler.configurations.PropertiesManager;
import it.erweb.crawler.weka.BandoObjValidator;

/**
 * A set of string parsing utility functions
 */
public class StringParser
{
	/**
	 * Searches the input string, trying to find the CIG
	 * 
	 * @param strToSearch
	 *            the ban body or publication html
	 * @return the effective CIG (like 7087137A75), or empty string if not found
	 */
	public static String tryGetCig(String strToSearch)
	{
		int MAX_CHARS_AFTER_CIG = 20;	//quanti max caratteri fra "cig: " e il cig vero
		String cig = "", cigPattern = PropertiesManager.BAN_CIG_PATTERN;
		int index = 0, offset = 0, chSaltati = 0, cigLength = PropertiesManager.BAN_CIG_LENGTH, i;
		char ch;

		try
		{
			if(strToSearch.toLowerCase().contains("cig"))
			{
				while(index != -1)
				{
					// si posiziona sulle occorrenze di "CIG"
					index = regexIndexOf(Pattern.compile(PropertiesManager.BAN_CIG_REGEX), strToSearch, offset);
					offset = index;

					// parte dal carattere dopo "CIG " e salva il cig: legge 2 char in pi�
					i=4;
					ch = strToSearch.charAt(index + i);
					
					String tmp = ch + "";
					//cig potrebbe essere fra parentesi o dopo "n.": manda avanti
					while((! tmp.matches("[0-9]")) && (chSaltati < MAX_CHARS_AFTER_CIG))
					{
						ch = strToSearch.charAt(index + (++i));
						tmp = ch + "";
						chSaltati++;
					}
					
					//legge il cig
					while(i < cigLength + 4 + chSaltati)
					{
						cig += ch;
						ch = strToSearch.charAt(index + (++i));
					}
					
					//toglie eventuali ) o spazi alla fine (non dovrebbe servire)
					cig = cig.replaceAll("[) ]", "");
 
					// controlla se l'ipotetico CIG appena ottenuto rispetta
					// il pattern
					if(!cig.matches(cigPattern))
					{
						cig = "";
						// prova con altre occorrenze di "CIG", se quello
						// trovato non va bene
						offset = index + i;
						index = regexIndexOf(Pattern.compile("CIG"), strToSearch, offset);
						offset = index;
					}
					else
					{
						index = -1;	//(break)
					}
				}
			}
			else
			{
				cig = "";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			cig = "";
		}

		return cig;
	}

	/**
	 * Searches the input to find the ban's external code
	 * 
	 * @param optionalInfo
	 *            the input string representing a fragment of publication html
	 * @return the actual code (like TX17BFC10379), or empty string if not found
	 */
	public static String tryGetCode(String optionalInfo)
	{
		String cod = "";
		int index = 0, offset = 0, i;
		char ch;
		String codPattern = PropertiesManager.BAN_CD_ESTERNO_PATTERN;

		try
		{
			while(index != -1)
			{
				i = 1;
				// si posiziona sull'occorrenza di '('
				index = optionalInfo.indexOf('(', offset);
				offset = index;

				// scorre tutta la stringa fino a ')'
				ch = optionalInfo.charAt(index + i);
				while(ch != ')')
				{
					cod += ch;
					ch = optionalInfo.charAt(index + (++i));
				}

				// controlla se l'ipotetico codice appena ottenuto rispetta il pattern
				if(!cod.matches(codPattern))
				{
					cod = "";
					//se no, prova con altre occorrenze di '('
					offset = index + i;
					index = optionalInfo.indexOf('(', offset);
					offset = index;
				}
				else
					break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			cod = "";
		}

		return cod;
	}

	/**
	 * Tries to find a valid Ban Object (a brief description) with different patterns, given the ban
	 * body
	 * 
	 * @param banBody
	 *            the whole ban
	 * @param pattern
	 *            different possible strings anticipating a ban description
	 * @return
	 */
	public static String tryGetObject(String banBody, String pattern)
	{
		int index = 0, offset = 0, i = 0;
		int maxChars = PropertiesManager.BAN_OBJ_MAX_CHARS, minChars = PropertiesManager.BAN_OBJ_MIN_CHARS;
		boolean probablyEnd;
		char current;
		
		//toglie maiuscole e spazi consecutivi
		String validBanBody = banBody.toLowerCase().replaceAll("[ ]+", " ");
		String ret = "";

		//cerca nel testo il pattern
		index = validBanBody.indexOf(pattern, offset);
		offset = pattern.length();
		while(index != -1)
		{
			// se ha trovato almeno una occorrenza:
			// prova a leggere fino a ".\n", ma solo se h letto almeno minChars caratteri
			probablyEnd = false;
			index += offset;
			
			//se trova il pattern quasi a fine bando, sara' sicuramente invalido
			if(index >= validBanBody.length())
				break;
			
			current = validBanBody.charAt(index++);
			// per sicurezza ci si ferma a maxChars
			while((i < maxChars) && (index < validBanBody.length()))
			{
				ret += current;

				if(current == '.')
					probablyEnd = true;
				else if(current == '\n' && probablyEnd && i >= minChars)
					break;

				current = validBanBody.charAt(index++);
				i++;
			}
			
			//rimuove caratteri che non c'entrano
			ret = StringParser.removeUseless(ret);
			
			// se WEKA trova l'oggetto valido, finisce;
			if(BandoObjValidator.validate(ret))
			{
				index = -1;	//(break)
				//prova a togliere righe lette superflue
				ret = tryDeleteLastLines(ret);
			}
			// altrimenti azzera e ricomincia con la prossima occorrenza
			else
			{
				ret = "";
				offset += index;
				index = validBanBody.indexOf(pattern, offset);
			}
		}

		return ret;
	}
	
	/**
	 * Searches the ban title to find a valid ban object
	 * 
	 * @param banBody
	 *            the whole ban
	 * @return a ban object if present, empty string otherwise
	 */
	public static String tryGetObjectTitle(String banBody)
	{
		//toglie maiuscole e spazi consecutivi
		String validBanBody = banBody.toLowerCase().replaceAll("[ ]+", " ");
		String ret = "";
		char current;
		int index = 0, i = 0;
		int maxChars = PropertiesManager.BAN_OBJ_MAX_TITLE_CHARS, minChars = PropertiesManager.BAN_OBJ_MIN_TITLE_CHARS;
		boolean probablyEnd = false;
		
		//legge i primi maxChars caratteri (o fino a .\n) (cioe' il titolo)
		current = validBanBody.charAt(index++);
		// per sicurezza ci si ferma a maxChars
		while(i < maxChars)
		{
			ret += current;

			if(current == '.')
				probablyEnd = true;
			else if(current == '\n' && probablyEnd && i >= minChars)
				break;

			current = validBanBody.charAt(index++);
			i++;
		}
		
		//rimuove caratteri che non c'entrano
		ret = StringParser.removeUseless(ret);
		
		//se trova un oggetto valido lo ritorna, altrimenti vuoto
		if(! BandoObjValidator.validate(ret))
		{
			ret = "";
		}

		return tryDeleteLastLines(ret);
	}

	/**
	 * removes useless strings in an hypothetical ban object
	 * 
	 * @param banObj
	 *            the ban object to be filtered
	 * @return the ban object without useless words
	 */
	private static String removeUseless(String banObj)
	{	
		String ret = "", replaced = "";
		
		//rimuove spazi fra i punto e accapo
		String lastValid = banObj.replaceAll(".\\s+\n", ".\n");
		
		//cancella ulteriori caratteri sporchi
		ret = lastValid.replaceAll(PropertiesManager.BAN_OBJ_JUNK_BODY, "");
		replaced = ret.replaceAll(PropertiesManager.BAN_OBJ_JUNK_HEAD, "");
		
		//continua a togliere parole in testa, finche' qualcosa fa match
		while(! (ret.equals(replaced)))
		{
			ret = replaced;
			replaced = ret.replaceAll(PropertiesManager.BAN_OBJ_JUNK_HEAD, "");
		}
		
		//toglie spazi consecutivi
		return ret.replaceAll("\\s{2,}|\\t", " ").trim();
	}

	/**
	 * 	Continuously tries to delete the last line of th ban object, only if the remaining String is still a valid object
	 * 
	 * @param banObj	the ban description
	 * @return			ban withouth useless lines
	 */
	private static String tryDeleteLastLines(String banObj)
	{
		String truncated = banObj, ret = banObj;
		int i = 0;
		int secondToLastLineIndex = ret.lastIndexOf(".\n");
		
		//continua a cancellare l'ultima riga, finche' e' ancora un oggetto valido
		try
		{
			truncated = ret.substring(0, secondToLastLineIndex);

			//cancella solo un max numero di volte
			while(BandoObjValidator.validate(truncated) && i < PropertiesManager.BAN_OBJ_PADDING_LINES)
			{
				ret = truncated;
				secondToLastLineIndex = ret.lastIndexOf('\n');
				truncated = ret.substring(0, secondToLastLineIndex);
				i++;
			}
		}
		catch(Exception e)
		{ }
		
		return ret.replace("\n", " ");
	}
	
	/**
	 * 	Converts a literal italian date in the explicit form ("2 luglio 2017") into a Java Util.Date
	 * 
	 * @param scadenza	string literal date
	 * @return	Date representing the string
	 */
	public static Date stringToDate(String scadenza)
	{
		Date date;
		SimpleDateFormat format;

		try
		{
			format = new SimpleDateFormat("d MMMM yyyy", Locale.ITALIAN);
			date = format.parse(scadenza);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			date = null;
		}

		return date;
	}
	
	/**
	 * 	Classic old 'indexOf' java function, but matches a regular expression (Pattern)
	 * 
	 * @param pattern	the regular expression to find
	 * @param s			the string in where search
	 * @param offset	s' starting search index
	 * @return			index of the first matching substring, -1 if nothing matches
	 */
	private static int regexIndexOf(Pattern pattern, String s, int offset)
	{
		String x = s.substring(offset, s.length() - 1);
	    Matcher matcher = pattern.matcher(x);
	    return matcher.find() ? matcher.start() + offset : -1;
	}
}
