package it.erweb.crawler.parser;

import java.util.ArrayList;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import it.erweb.crawler.configurations.PropertiesManager;
import it.erweb.crawler.model.Bando;

/**
 * Searches html for URLs or specific info 
 */
public class HtmlParser
{
	
	/**
	 * Gets the publications home page, given the gazzetta home page
	 * 
	 * @param html	gazzetta home page html
	 * @return		a String representing the publications URL
	 */
	public static String getHomePublicationsURL(String html)
	{
		String ret = "";
		int startIndex;
		char current;
		
		startIndex = html.indexOf(PropertiesManager.HOME_PUBLICATIONS_PATTERN);
		current = html.charAt(startIndex);
		while(current != '\"')
		{
			ret += current;
			current = html.charAt(++startIndex);
		}
		
		return PropertiesManager.GAZZETTA_HOME_URL + ret;
	}
	
	/**
	 * Gets all the publications URLs, given the publications home page
	 * 
	 * @param html	publications page
	 * @return		A list of publications URLs
	 */
	public static ArrayList<String> getPublicationsURL(String html)
	{
		ArrayList<String> ret = new ArrayList<String>();
		int startIndex = 0, offset = 0;
		char current;
		String url = "";
		
		startIndex = html.indexOf(PropertiesManager.PUBLICATION_BAN_PATTERN, offset);
		while(startIndex != -1)
		{
			current = html.charAt(startIndex);
			while(current != '\"')
			{
				url += current;
				current = html.charAt(++startIndex);
			}
			
			ret.add(PropertiesManager.GAZZETTA_HOME_URL + url);
			offset = startIndex;
			startIndex = html.indexOf(PropertiesManager.PUBLICATION_BAN_PATTERN, offset);
			url = "";
		}
		
		return ret;
	}

	/**
	 * Parses the publication in order to extract all its Bans' available informations
	 * 
	 * @param pub	html of a publication page
	 * @return		a list of Bans' URLs contained in pub
	 */
	public static ArrayList<Bando> getPublicationBans(String pub)
	{
		ArrayList<Bando> bandi = new ArrayList<Bando>();
		int i = 4, j=0;
		Element bando;

		//primo parsing per arrivare a elenco bandi
		Document doc = Jsoup.parseBodyFragment(pub);		//tutto l'html
		Element mainContent = doc.body().child(20);			//<div class="main_content">
		Element container = mainContent.child(0);			//<div class="container_16"> 
		Element elencoBandi = container.child(0);			//<div id="elenco_hp"> 
		String tipoBando = elencoBandi.child(2).ownText();				//avvisi e bandi di gara
		Element emettitore = elencoBandi.child(3);			//<span class="emettitore"> MINISTERI..
		String tipoRichiedente = emettitore.ownText();		//emettitore senza <span...

		//parte dalla riga dopo emettitore, i=4 (un bando)
		while(i< elencoBandi.children().size())
		{
			bando = elencoBandi.child(i++);					//prende la riga di un "bando" (sporca)
			
			//primi 24 chars: <span class="risultato"> | <span class="emettitore"> | <span class="rubrica">
			String spanType = bando.toString().substring(0, 25).toLowerCase();
			//se trova una riga con nuovo emettitore, aggiorna tipoRichiedente e salta subito alla riga dopo
			if(spanType.contains("emettitore"))
			{
				tipoRichiedente = bando.ownText();
				continue;
			}
			else if(spanType.contains("rubrica"))
			{
				tipoBando = bando.ownText();
				continue;
			}
			
			Element bandoSenzaSpan = bando.child(0);
			//prende tutta la riga ancora sporca del bando e splitta dove trova '"': nella seconda cella c'e' l'URL
			String url = PropertiesManager.GAZZETTA_HOME_URL + bandoSenzaSpan.toString().split("\"")[1];
			
			//toglie anche <a href... e rimane il nome richiedente e scadenza
			Element data = bandoSenzaSpan.child(0);	
			
			//prende l'ultimo elemento in riga (fra span, a href, font e altro) e ne estrae solo il testo (scadenza)		
			//ma non in tutti i bandi è presente
			int dataLength = data.children().size();
			if(dataLength > 1)
			{			
				String tmp = data.child(data.children().size() - 1).ownText();	
				try
				{
					String scadenza = tmp.substring(8, tmp.length() - 1);			//toglie le parentesi e "scad."
				}
				catch(StringIndexOutOfBoundsException e)
				{
					//non c'e' scadenza
				}
			}
			//toglie tutti i tag da data: rimane nome richiedente
			String nmRichiedente = data.ownText();
			
			//a questo punto si conoscono: tipoRichedente (cambia più volte nel bando), nomeRichiedente, url, scadenza.
			Bando b = new Bando();
			b.setStato("DA_PARSIFICARE");
			b.setTipo(tipoBando);
			b.setTiporichiedente(tipoRichiedente);
			b.setNmRichiedente(nmRichiedente);
			b.setUrl(url);
			//da parsificare data testuale e trasformarla in Date
			//b.setScadenza();
			
			//da continuare per altri tipoRichiedenti, e da continuare per altri tipi di bando (avvisi gara, gare scadute...)
			
			bandi.add(b);
		}
		
		
		return bandi;
	}
}
