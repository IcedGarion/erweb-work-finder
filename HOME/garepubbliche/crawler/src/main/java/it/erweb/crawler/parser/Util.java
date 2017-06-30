package it.erweb.crawler.parser;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import it.erweb.crawler.configurations.PropertiesManager;

public class Util
{
	public static String tryGetCig(String optionalInfo)
	{
		String cig = "", cigPattern = PropertiesManager.BAN_CIG_PATTERN;
		int index = 0, offset = 0, cigLength = PropertiesManager.BAN_CIG_LENGTH, i = 4;
		char ch;
		
		try
		{
			if(optionalInfo.contains("CIG"))
			{
				while(index != -1)
				{
					// si posiziona sulle occorrenze di "CIG"
					index = optionalInfo.indexOf("CIG", offset);
					offset = index;

					//parte dal carattere dopo "CIG " e salva il cig
					ch = optionalInfo.charAt(index + i);
					while(i < cigLength + 4)
					{
						cig += ch;
						ch = optionalInfo.charAt(index + (++i));
					}

					// controlla se l'ipotetico CIG appena ottenuto rispetta
					// il pattern
					if(!cig.matches(cigPattern))
						cig = "";

					// prova con altre occorrenze di "CIG"
					offset = index + i;
					index = optionalInfo.indexOf("CIG", offset);
					offset = index;
				}
			}
			else
				cig = "";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			cig = "";
		}
		
		return cig;
	}

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
				//si posiziona sulle occorrenze di '('
				index = optionalInfo.indexOf('(', offset);
				offset = index;
				
				//scorre tutta la stringa fino a ')' o fino a sforare la lunghezza del codice
				ch = optionalInfo.charAt(index + i);
				while((ch != ')') && (i < codPattern.length()))
				{
					cod += ch;
					ch = optionalInfo.charAt(index + (++i));
				}
				
				//controlla se l'ipotetico codice appena ottenuto rispetta il pattern
				if(! cod.matches(codPattern))
					cod = "";
					
				//prova con altre occorrenze di '('
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
}
