package it.erweb.crawler.parser;

import java.util.ArrayList;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import it.erweb.crawler.configurations.PropertiesManager;
import it.erweb.crawler.dbManager.repository.BandoRepository;
import it.erweb.crawler.dbManager.repository.PubblicazioneRepository;
import it.erweb.crawler.model.Bando;
import it.erweb.crawler.model.Pubblicazione;

/**
 * Searches htmls for specific infos 
 */
public class HtmlParser
{
	
	/**
	 * Gets the publications home page, given the Gazzetta home page
	 * 
	 * @param html	gazzetta home page html
	 * @return		a String representing the publications page URL
	 */
	public static String getHomePublicationsURL(String html)
	{
		String ret = "";
		int startIndex;
		char current;
		
		startIndex = html.indexOf(PropertiesManager.PUBLICATIONS_HOME_PATTERN);
		current = html.charAt(startIndex);
		while(current != '\"')
		{
			ret += current;
			current = html.charAt(++startIndex);
		}
		
		return PropertiesManager.GAZZETTA_HOME_URL + ret;
	}
	
	/**
	 * Gets all the publications infos, given the publications home page
	 * 
	 * @param html	publications page
	 * @return		A list of publications
	 */
	public static ArrayList<Pubblicazione> getPublications(String html)
	{
		//PubblicazioneRepository repository = new PubblicazioneRepository();
		ArrayList<Pubblicazione> pubblicazioni = new ArrayList<Pubblicazione>();
		int startIndex = 0, offset = 0, nmIndex = 0, numPub = -1;
		char current;
		String url = "", strNumPub;
		Pubblicazione pub;
		
		//va alla prima occorrenza dell'url pattern
		startIndex = html.indexOf(PropertiesManager.PUBLICATION_BAN_PATTERN);
		while(startIndex != -1)
		{
			//aggiunge URL dall'inizio di "" fino alla fine di ""
			current = html.charAt(startIndex);
			while(current != '\"')
			{
				url += current;
				current = html.charAt(++startIndex);
			}
			
			//scorre ancora un po' avanti per trovare relativo numero di pubblicazione (che è sempre presente)
			nmIndex = html.indexOf(PropertiesManager.PUBLICATION_NUMBER_PATTERN, startIndex)
					 + PropertiesManager.PUBLICATION_NUMBER_PATTERN.length();
			current = html.charAt(nmIndex);
			strNumPub = "";
			while(true)
			{
				try
				{
					//prova a leggere carttere per carattere, controlla se è INT e lo aggiunge al totale numPub
					Integer.parseInt("" + current);
					strNumPub += current;
					current = html.charAt(++nmIndex);
				}
				catch(Exception e)
				{
					break;
				}
			}
			//prova a trasformare il numero letto
			try
			{
				numPub = Integer.parseInt(strNumPub);
			}
			catch(Exception e)
			{
				numPub = -1;
			}
				
			
			//crea nuova pubblicazione con tutti i dati raccolti
			pub = new Pubblicazione();
			pub.setDtInserimento(new Date());
			if(numPub != -1)
				pub.setNmPubblicazione(numPub);
			pub.setStato("DA_SCARICARE");
			pub.setUrl(PropertiesManager.GAZZETTA_HOME_URL + url);
			pubblicazioni.add(pub);
			
			//SALVA NEL DB
			//repository.create(pub);
			
			
			//ricomincia per trovare altre pubblicazioni
			offset = startIndex;
			startIndex = html.indexOf(PropertiesManager.PUBLICATION_BAN_PATTERN, offset);
			url = "";
		}
		
		return pubblicazioni;
	}

	/**
	 * Parses the publication in order to extract all its Bans' available informations
	 * 
	 * @param publicationHtml	html of a publication page
	 * @param pubblicazione 	the referenced Pubblicazione containing the Bans
	 * @return					a list of Bans contained in pub
	 */
	public static ArrayList<Bando> getPublicationBans(String publicationHtml, Pubblicazione pubblicazione)
	{
		//BandoRepository repository = new BandoRepository();
		ArrayList<Bando> bandi = new ArrayList<Bando>();
		Bando b;
		int i = 4, dataLength;
		Element bando, bandoSenzaSpan, data;
		String codEsterno = "", cig = "", url, scadenza = "", tmp, spanType, optionalInfo, nmRichiedente;

		//primo parsing per arrivare a elenco bandi
		Document doc = Jsoup.parseBodyFragment(publicationHtml);		//tutto l'html
		Element mainContent = doc.body().child(20);			//<div class="main_content">
		Element container = mainContent.child(0);			//<div class="container_16"> 
		Element elencoBandi = container.child(0);			//<div id="elenco_hp"> 
		String tipoBando = elencoBandi.child(2).ownText();				//avvisi e bandi di gara
		Element emettitore = elencoBandi.child(3);			//<span class="emettitore"> MINISTERI..
		String tipoRichiedente = emettitore.ownText();		//emettitore senza <span...

		//parte dalla riga dopo emettitore, i=4 (un bando)
		while(i < elencoBandi.children().size())
		{
			bando = elencoBandi.child(i++);					//prende la riga di un "bando" (sporca)
			
			//primi 24 chars: <span class="risultato"> | <span class="emettitore"> | <span class="rubrica">
			spanType = bando.toString().substring(0, 25).toLowerCase();
			
			//se trova una riga con nuovo emettitore, aggiorna tipoRichiedente e salta subito alla riga dopo
			if(spanType.contains("emettitore"))
			{
				tipoRichiedente = bando.ownText();
				continue;
			}
			//se trova nuovo tipo di bando, aggiorna e salta la riga
			else if(spanType.contains("rubrica"))
			{
				tipoBando = bando.ownText();
				continue;
			}
			
			bandoSenzaSpan = bando.child(0);
			
			//prova a estrarre cig e codice bando
			optionalInfo = bando.child(1).ownText();
			codEsterno = Util.tryGetCode(optionalInfo);
			cig = Util.tryGetCig(optionalInfo);
			
			
			//prende tutta la riga ancora sporca del bando e splitta dove trova '"': nella seconda cella c'e' l'URL
			url = PropertiesManager.GAZZETTA_HOME_URL + bandoSenzaSpan.toString().split("\"")[1];
			url = url.replace("&amp;", "&");	//rimuove caratteri che poi spariscono (?)
			
			//toglie anche <a href... e rimane il nome richiedente e scadenza
			data = bandoSenzaSpan.child(0);	
			
			//toglie tutti i tag da data: rimane nome richiedente
			nmRichiedente = data.ownText();
			
			//prende l'ultimo elemento in riga (fra span, a href, font e altro) e ne estrae solo il testo (scadenza)		
			//ma non in tutti i bandi è presente
			dataLength = data.children().size();
			if(dataLength > 1)
			{			
				tmp = data.child(data.children().size() - 1).ownText();	
				try
				{
					scadenza = tmp.substring(7, tmp.length() - 1);			//toglie le parentesi e "scad."
				}
				catch(StringIndexOutOfBoundsException e)
				{
					//non c'e' scadenza
					scadenza = "";
				}
			}
			else
				scadenza = "";

			
			//crea un nuovo bando con tutte le info raccolte
			b = new Bando();
			b.setStato("DA_PARSIFICARE");
			b.setTipo(tipoBando);
			b.setTiporichiedente(tipoRichiedente);
			b.setNmRichiedente(nmRichiedente);
			b.setUrl(url);
			b.setDtInserimento(new Date());
			if(codEsterno != "")
				b.setCdEsterno(codEsterno);
			if(cig != "")
				b.setCig(cig);
			if(scadenza != "")
				b.setScadenza(Util.stringToDate(scadenza));
			b.setPubblicazione(pubblicazione);
			
			bandi.add(b);
			
			//SALVA NEL DB
			//repository.create(b);
		}	
		
		return bandi;
	}

	public static void parseBan(Bando ban)
	{
		Document doc = Jsoup.parseBodyFragment(ban.getTesto());
		Element mainContent = doc.body();
		Element divBando = mainContent.child(12);
		String testoBando = divBando.text();

		//aggiorna il testo del bando con quello vero
		ban.setTesto(testoBando);
		
		return;
	}
}
