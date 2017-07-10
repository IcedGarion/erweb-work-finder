package it.erweb.crawler.parser;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import it.erweb.crawler.configurations.PropertiesManager;
import it.erweb.crawler.weka.BandoValidator;

/**
 * A set of parsing utility functions
 */
public class Util
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
		String cig = "", cigPattern = PropertiesManager.BAN_CIG_PATTERN;
		int index = 0, offset = 0, cigLength = PropertiesManager.BAN_CIG_LENGTH, i = 4;
		char ch;

		try
		{
			if(strToSearch.contains("CIG"))
			{
				while(index != -1)
				{
					// si posiziona sulle occorrenze di "CIG"
					index = strToSearch.indexOf("CIG", offset);
					offset = index;

					// parte dal carattere dopo "CIG " e salva il cig
					ch = strToSearch.charAt(index + i);
					while(i < cigLength + 4)
					{
						cig += ch;
						ch = strToSearch.charAt(index + (++i));
					}

					// controlla se l'ipotetico CIG appena ottenuto rispetta
					// il pattern
					if(!cig.matches(cigPattern))
					{
						cig = "";
						// prova con altre occorrenze di "CIG", se quello
						// trovato non va bene
						offset = index + i;
						index = strToSearch.indexOf("CIG", offset);
						offset = index;
					}
					else
					{
						break;
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
	 * Searches the input to find the ban code
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
				// si posiziona sulle occorrenze di '('
				index = optionalInfo.indexOf('(', offset);
				offset = index;

				// scorre tutta la stringa fino a ')' o fino a sforare la
				// lunghezza del codice
				ch = optionalInfo.charAt(index + i);
				while((ch != ')') && (i < codPattern.length()))
				{
					cod += ch;
					ch = optionalInfo.charAt(index + (++i));
				}

				// controlla se l'ipotetico codice appena ottenuto rispetta il
				// pattern
				if(!cod.matches(codPattern))
				{
					cod = "";
				}

				// prova con altre occorrenze di '('
				offset = index + i;
				index = optionalInfo.indexOf('(', offset);
				offset = index;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			cod = "";
		}

		return cod;
	}

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
	 * removes useless strings in an hypothetical ban object
	 * 
	 * @param banObj		the ban object to be filtered
	 * @return	the ban object without useless words
	 */
	public static String removeUseless(String banObj)
	{
		//rimuove spazi fra i punto e accapo
		String lastValid = banObj.replaceAll(".[ ]+\n", ".\n"), truncated = "";
		int secondToLastLineIndex = lastValid.lastIndexOf(".\n");

		//rimuove . e : iniziali e spazi
		if(lastValid.startsWith(":") || lastValid.startsWith("."))
			lastValid = lastValid.substring(1, lastValid.length()).trim();
		
		try
		{
			truncated = lastValid.substring(0, secondToLastLineIndex);

			//continua a cancellare l'ultima riga, finche' e' ancora un oggetto valido
			while(BandoValidator.validate(truncated))
			{
				lastValid = truncated;
				secondToLastLineIndex = lastValid.lastIndexOf('\n');
				truncated = lastValid.substring(0, secondToLastLineIndex);
			}
		}
		catch(Exception e)
		{ }
		//lastValid e' l'ultimo oggetto ancora valido (dopo ipotetici troncamenti):
		//cancella ulteriori caratteri sporchi
		return lastValid
				.replaceAll("sezione+\\s+[iii|3|ii|2|1|i]+\\s"
						+ "|(i[.])|(ii[.])|(iii[.])|(iv[.])|(v[.])|(vi[.])|(vii[.])|(viii[.])|(ix[.])|(x[.])"
						+ "|[[.1]|[.2]|[.3]|[.4]|[.5]|[.6]|[.7]|[.8]|[.9]]+[)]", "")
				.replace("\n", " ").trim();
	}

	/**
	 * Tries to find a valid Ban Object (a brief description), given the ban
	 * body for different patterns
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
			// prova a leggere fino a ".\n", ma solo se h letto almeno minChars
			// caratteri
			probablyEnd = false;
			index += offset;
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
			ret = Util.removeUseless(ret);
			
			// se WEKA trova l'oggetto valido, finisce;
			if(BandoValidator.validate(ret))
			{
				index = -1;
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
}
