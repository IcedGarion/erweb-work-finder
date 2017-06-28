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
	 * Parses the publication in order to extract all its Bans' URLs
	 * 
	 * @param pub	html of a publication page
	 * @return		a list of Bans' URLs contained in pub
	 */
	/**
	 * @param pub
	 * @return
	 */
	public static ArrayList<String> getPublicationBansURL(String pub)
	{
		ArrayList<String> bandiUrls = new ArrayList<String>();
		int i = 4, j=0;
		Element bando;

		//primo parsing per arrivare a elenco bandi
		Document doc = Jsoup.parseBodyFragment(pub);		//tutto l'html
		Element mainContent = doc.body().child(20);			//<div class="main_content">
		Element container = mainContent.child(0);			//<div class="container_16"> 
		Element elencoBandi = container.child(0);			//<div id="elenco_hp"> 
		Element emettitore = elencoBandi.child(3);			//<span class="emettitore"> MINISTERI..
		String tipoRichiedente = emettitore.ownText();		//emettitore senza <span...

		//parte dalla riga dopo emettitore, i=4 (un bando)
		while(true)
		{
			bando = elencoBandi.child(i++);					//prende la riga di un bando (sporca)
			//primi 24 chars: <span class="risultato"> | <span class="emettitore">
			
			
			
			//SE EMETTITORE, BISOGNA AGGIORNARE TIPORICHIEDENTE!
			
			
			
			if(bando.toString().substring(0, 25).contains("emettitore"))
				break;
			
			Element bandoSenzaSpan = bando.child(0);
			//prende tutta la riga ancora sporca del bando e splitta dove trova '"': nella seconda cella c'e' l'URL
			String url = PropertiesManager.GAZZETTA_HOME_URL + bandoSenzaSpan.toString().split("\"")[1];
			
			//toglie anche <a href... e rimane il nome richiedente e scadenza
			Element data = bandoSenzaSpan.child(0);	
			
			//prende l'ultimo elemento in riga (fra span, a href, font e altro) e ne estrae solo il testo (scadenza)
			String tmp = data.child(data.children().size() - 1).ownText();	
			String scadenza = tmp.substring(8, tmp.length() - 1);			//toglie le parentesi e "scad."
			
			//toglie tutti i tag da data: rimane nome richiedente
			String nmRichiedente = data.ownText();
			
			//a questo punto si conoscono: tipoRichedente (cambia più volte nel bando), nomeRichiedente, url, scadenza.
			Bando b = new Bando();
			b.setStato("Da_PARSIFICARE");
			b.setTiporichiedente(tipoRichiedente);
			b.setNmRichiedente(nmRichiedente);
			b.setUrl(url);
			//da parsificare data testuale e trasformarla in Date
			//b.setScadenza();
			
			//da continuare per altri tipoRichiedenti, e da continuare per altri tipi di bando (avvisi gara, gare scadute...)
			
			bandiUrls.add(url);
		}
		
		
		return bandiUrls;
	}
}
