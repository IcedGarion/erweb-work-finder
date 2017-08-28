package it.erweb.crawler.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import it.erweb.crawler.configurations.PropertiesManager;
import it.erweb.crawler.dbManager.JPAManager;
import it.erweb.crawler.dbManager.repository.BandoRepository;
import it.erweb.crawler.dbManager.repository.PubblicazioneRepository;
import it.erweb.crawler.httpClientUtil.HttpGetter;
import it.erweb.crawler.httpClientUtil.Notifier;
import it.erweb.crawler.main.Main;
import it.erweb.crawler.model.Bando;
import it.erweb.crawler.model.Pubblicazione;

/**
 * Searches htmls for specific infos 
 */
public class HtmlParser
{	
	private static Logger logger = Logger.getLogger(Main.class.getName());
	
	/**
	 * Gets all the publications' infos, given the publications home page, and stores them persistently on the DB
	 * 
	 * @param html	publications page
	 * @return		True if a new publication is available
	 * @throws ParseException 
	 */
	public static boolean getPublications(String html) throws ParseException
	{
		boolean ret = false;
		int startIndex = 0, offset = 0, nmIndex = 0, numPub = -1, i = 0;
		char current;
		String url, strNumPub, strDate;
		Pubblicazione pub;
		Date pubDate, lastPubDate = PubblicazioneRepository.getLastDate();
		
		//va alla prima occorrenza dell'url pattern
		startIndex = html.indexOf(PropertiesManager.PUBLICATION_DETAIL_PATTERN);
		while(startIndex != -1)
		{
			url = strNumPub = strDate = "";
			i = 0;
			
			//salva URL dall'inizio di "" fino alla fine di ""
			current = html.charAt(startIndex);
			while(current != PropertiesManager.GAZZETTA_URL_TERMINATOR)
			{
				url += current;
				current = html.charAt(++startIndex);
				
				if((i++) > PropertiesManager.PUBLICATIONS_URL_LENGTH)
				{
					System.err.println("URL terminators may have changed: " + PropertiesManager.GAZZETTA_URL_TERMINATOR);
					Notifier.notifyDev("URL terminators may have changed: " + PropertiesManager.GAZZETTA_URL_TERMINATOR);
					System.exit(2);
				}
			}
			
			//scorre ancora un po' avanti per trovare relativo numero di pubblicazione (che è sempre presente)
			nmIndex = html.indexOf(PropertiesManager.PUBLICATION_NUMBER_PATTERN, startIndex)
					 + PropertiesManager.PUBLICATION_NUMBER_PATTERN.length();
			current = html.charAt(nmIndex);
			
			//aggiunge numero pubblicazione
			while(true)
			{
				try
				{
					//prova a leggere carttere per carattere, controlla se è INT e lo aggiunge al totale numPub
					Integer.parseInt("" + current);
					strNumPub += current;
					current = html.charAt(++nmIndex);
				}
				//quando arriva al carattere che non e' piu' un int, ha finito di leggere il numero
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
				
			//appena dopo il numero c'e' la Data in cui la pubblicazione e' stata inserita
			nmIndex += 5;
			while(strDate.length() < 10)
			{
				current = html.charAt(nmIndex++);
				if(current != '\n' && current != ' ' && current != '\t')
					strDate += current;
			}
			pubDate = new SimpleDateFormat("dd-MM-yyyy").parse(strDate);
			
			//controllo date: inserisce soltanto se trova una NUOVA pubblicazione SCARICATA (data maggiore di tutte quelle nel db)
			if(pubDate.after(lastPubDate))
			{
				ret = true;
				//crea nuova pubblicazione con tutti i dati raccolti
				pub = new Pubblicazione();									
				pub.setDtInserimento(new Date());							//DT_INSERIMENTO
				if(numPub != -1)
				{
					pub.setNmPubblicazione(numPub);							//NM_PUBBLICAZIONE
				}
				pub.setStato("DA_SCARICARE");								//STATO
				pub.setUrl(PropertiesManager.GAZZETTA_HOME_URL + url);		//URL
						
				//SALVA NEL DB
				JPAManager.create(pub);
			}
			
			//ricomincia il ciclo per trovare altre pubblicazioni
			offset = startIndex;
			startIndex = html.indexOf(PropertiesManager.PUBLICATION_DETAIL_PATTERN, offset);
			url = "";
		}
		
		return ret;
	}

	/**
	 * Parses the publication in order to extract all its Bans' available informations
	 * 
	 * @param publicationHtml	html of a publication page
	 * @param pubblicazione 	the referenced Pubblicazione containing the Bans
	 */
	public static void getPublicationBans(String publicationHtml, Pubblicazione pubblicazione)
	{
		Bando b;
		int i = 4, dataLength;
		Element bando, bandoSenzaSpan, data;
		String publicationHtmlCpy = publicationHtml, tipoBando = "", codEsterno = "", cig = "", url = "", scadenza = "", tmp, spanType, optionalInfo, nmRichiedente;
		Element doc, elencoBandi = null;
		
		//primo parsing per arrivare a elenco bandi: causa possobili fallimenti, cicla
		while(tipoBando.equals(""))
		{
			doc = Jsoup.parseBodyFragment(publicationHtmlCpy).body();		//tutto l'html
			elencoBandi = doc.getElementById(PropertiesManager.PUBLICATION_BAN_DIVID_PATTERN);			//<div id="elenco_hp"> 
			
			//potrebbe fallire se html e' corrotto: lo riscarica
			try
			{
				tipoBando = elencoBandi.child(2).ownText();				//avvisi e bandi di gara7
			}
			catch(Exception e)
			{
				publicationHtmlCpy = HttpGetter.get(pubblicazione.getUrl());
			}
		}
		
		
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
			//se trova nuovo tipo di bando (avvisi esiti, avvisi annullamenti..), si ferma
			else if(spanType.contains("rubrica"))
			{
				break;
			}
			
			bandoSenzaSpan = bando.child(0);
			
			//prova a estrarre cig e codice bando
			optionalInfo = bando.child(1).ownText();
			codEsterno = StringParser.tryGetCode(optionalInfo);
			cig = StringParser.tryGetCig(optionalInfo);
			
			
			//prende tutta la riga ancora sporca del bando e splitta dove trova '"': nella seconda cella c'e' l'URL
			try
			{
				url = PropertiesManager.GAZZETTA_HOME_URL + bandoSenzaSpan.toString().split(""+PropertiesManager.GAZZETTA_URL_TERMINATOR)[1];
			}
			catch(Exception e)
			{
				System.err.println("URL terminators may have changed: " + PropertiesManager.GAZZETTA_URL_TERMINATOR);
				Notifier.notifyDev("URL terminators may have changed: " + PropertiesManager.GAZZETTA_URL_TERMINATOR);
				System.exit(2);
			}
			
			url = url.replace("&amp;", "&");	//rimuove caratteri che poi spariscono (?)
			
			//toglie anche <a href... e rimane il nome richiedente e scadenza
			data = bandoSenzaSpan.child(0);	
			
			//toglie tutti i tag da data: rimane nome richiedente
			nmRichiedente = data.ownText();
			
			//prende l'ultimo elemento in riga (fra span, a href, font e altro) e ne estrae solo il testo (scadenza)		
			//ma non in tutti i bandi è presente
			dataLength = data.children().size();
			if(dataLength >= 1)
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
			{
				scadenza = "";
			}
			
			//crea un nuovo bando con tutte le info raccolte
			b = new Bando();
			if(codEsterno != "")
				b.setCdEsterno(codEsterno);							//CD_ESTERNO
			b.setPubblicazione(pubblicazione);						//CD_PUBBLICAZIONE
			if(cig != "")
				b.setCig(cig);										//CIG
			b.setTipo(tipoBando);									//TIPO
			b.setTiporichiedente(tipoRichiedente);					//TIPORICHIEDENTE
			b.setNmRichiedente(nmRichiedente);						//NM_RICHIEDENTE
			if(scadenza != "")	
				b.setScadenza(StringParser.stringToDate(scadenza));	//SCADENZA
			b.setUrl(url);											//URL
			b.setStato("DA_PARSIFICARE");							//STATO
			b.setDtInserimento(new Date());							//DT_INSERIMENTO
			
			try
			{
				//SALVA NEL DB
				JPAManager.create(b);
			}
			catch(Exception e)
			{	
				//se ci sono errori nell'inserimento in db, esempio:
				//cerca di inserire un bando già inserito. Allora lo salta e basta
				logger.info("Ban " + codEsterno + " already in the db!");
			}
		}	
		
		//ricavati tutti i bandi di una pubblicazione, aggiorna lo stato
		PubblicazioneRepository.updateState(pubblicazione, "SCARICATA");
	}
	
	/**
	 * Searches the Ban body, trying to find the Ban Object and Cig, and then updates the Ban properties in the DB
	 * 
	 * @param ban	html of the whole Ban page
	 */
	public static void parseBan(Bando ban)
	{
		String cig = "", oggetto = "";
		int i = 0, patternLength = PropertiesManager.BAN_OBJ_PATTERNS.length;
		Document doc;
		Element mainContent;
		Element divBando = null;
		Elements tmp;
		
		//causa possibili fallimenti, cicla
		while(divBando == null)
		{
			//se non riesce a leggere bene html, lo riscarica
			try
			{
				doc = Jsoup.parseBodyFragment(ban.getTesto());
				mainContent = doc.body();
				tmp = mainContent.getElementsByClass(PropertiesManager.BAN_DIVCLASS);
				divBando = tmp.get(0);
			}
			catch(Exception e)
			{
				//riscarica il ban e setta il testo da nuovo
				ban.setTesto(HttpGetter.get(ban.getUrl()));
			}
		}
		
		String testoBando = divBando.text();
		//aggiorna il testo del bando con quello vero (prima era tutto l'html)
		BandoRepository.updateText(ban, testoBando);		
		
		//estrae CIG, se non e' gia' presente
		if(ban.getCig() == null)
		{
			cig = StringParser.tryGetCig(testoBando);
			if(!cig.equals(""))
			{
				BandoRepository.updateCig(ban, cig);
			}
		}

		//estrae OGGETTO
		try
		{
			//prova diversi "pattern", finche' non trova un oggetto valido o finche' non li ha provati tutti
			while((oggetto.equals("")) && (i < patternLength))
			{
				oggetto = StringParser.tryGetObject(testoBando, PropertiesManager.BAN_OBJ_PATTERNS[i]);
				i++;
			}
			//se ha finito tutti i pattern ma ancora non ha trovato niente, prova a cercare ne titolo
			if(oggetto.equals(""))
			{
				oggetto = StringParser.tryGetObjectTitle(testoBando);
			}
		}
		catch(Exception e)
		{
			// lascia oggetto a null, ma ha comunque provato a parsificare il bando
		}
		finally
		{
			if(oggetto != "")
			{
				BandoRepository.updateObject(ban, oggetto);
			}
			
			BandoRepository.updateState(ban, "PARSIFICATO");
		}
		
		return;
	}
}
