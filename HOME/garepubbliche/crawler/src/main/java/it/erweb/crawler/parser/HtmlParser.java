package it.erweb.crawler.parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	 * Gets all the publications infos, given the publications home page
	 * 
	 * @param html	publications page
	 * @return		True if a new publication is available
	 * @throws ParseException 
	 */
	public static boolean getPublications(String html) throws ParseException
	{
		boolean ret = false;
		int startIndex = 0, offset = 0, nmIndex = 0, numPub = -1;
		char current;
		String url, strNumPub, strDate;
		Pubblicazione pub;
		Date pubDate, lastPubDate = PubblicazioneRepository.getLastDate();
		
		//va alla prima occorrenza dell'url pattern
		startIndex = html.indexOf(PropertiesManager.PUBLICATION_DETAIL_PATTERN);
		while(startIndex != -1)
		{
			url = strNumPub = strDate = "";
			
			//salva URL dall'inizio di "" fino alla fine di ""
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
				
			//dopo il numero c'e' la Data in cui la pubblicazione e' stata inserita
			nmIndex += 5;
			while(strDate.length() < 10)
			{
				current = html.charAt(nmIndex++);
				System.out.print(current);
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
				PubblicazioneRepository.create(pub);
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
		String codEsterno = "", cig = "", url, scadenza = "", tmp, spanType, optionalInfo, nmRichiedente;

		//primo parsing per arrivare a elenco bandi
		Element doc = Jsoup.parseBodyFragment(publicationHtml).body();		//tutto l'html
		Element elencoBandi = doc.getElementById(PropertiesManager.PUBLICATION_BAN_DIVID_PATTERN);			//<div id="elenco_hp"> 
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
				
			//SALVA NEL DB
			BandoRepository.create(b);
			}	
		
		//ricavati tutti i bandi di una pubblicazione, aggiorna lo stato
		PubblicazioneRepository.updateState(pubblicazione, "SCARICATA");
	}
	
	/**
	 * Searches the Ban body, trying to find the Ban Object
	 * 
	 * @param ban	html of the whole Ban page
	 */
	public static void parseBan(Bando ban)
	{
		String cig = "", oggetto = "";
		int i = 0, timeout = PropertiesManager.SYS_BAN_PARSING_TRIALS_TIMEOUT, patternLength = PropertiesManager.BAN_OBJ_PATTERNS.length;
		Document doc = Jsoup.parseBodyFragment(ban.getTesto());
		Element mainContent = doc.body();
		Element divBando;
		
		//codice soggetto ad errori
		while(true)
		{
			try
			{
				divBando = mainContent.getElementsByClass(PropertiesManager.BAN_DIVCLASS).get(0);
				break;
			}
			catch(Exception e)
			{
				if(--timeout <= 0)
				{
					System.err.println("Unable to parse Ban");
					System.exit(1);
				}
				
				continue;
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
			while((oggetto == "") && (i < patternLength))
			{
				oggetto = StringParser.tryGetObject(testoBando, PropertiesManager.BAN_OBJ_PATTERNS[i]);
				i++;
			}
			//se ha finito tutti i pattern ma ancora non ha trovato niente, prova a cercare ne titolo
			if(oggetto == "")
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
