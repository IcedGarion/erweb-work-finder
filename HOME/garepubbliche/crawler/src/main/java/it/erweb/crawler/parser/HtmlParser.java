package it.erweb.crawler.parser;

import java.util.ArrayList;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import it.erweb.crawler.configurations.PropertiesManager;
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
		startIndex = html.indexOf(PropertiesManager.PUBLICATION_DETAIL_PATTERN);
		while(startIndex != -1)
		{
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
			{
				pub.setNmPubblicazione(numPub);
			}
			pub.setStato("DA_SCARICARE");
			pub.setUrl(PropertiesManager.GAZZETTA_HOME_URL + url);
			
			pubblicazioni.add(pub);
			
			//SALVA NEL DB
			//repository.create(pub);
			
			
			//ricomincia per trovare altre pubblicazioni
			offset = startIndex;
			startIndex = html.indexOf(PropertiesManager.PUBLICATION_DETAIL_PATTERN, offset);
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
			{
				scadenza = "";
			}
			
			
			//crea un nuovo bando con tutte le info raccolte
			b = new Bando();
			if(codEsterno != "")
				b.setCdEsterno(codEsterno);					//CD_ESTERNO
			b.setPubblicazione(pubblicazione);				//CD_PUBBLICAZIONE
			if(cig != "")
				b.setCig(cig);								//CIG
			b.setTipo(tipoBando);							//TIPO
			b.setTiporichiedente(tipoRichiedente);			//TIPORICHIEDENTE
			b.setNmRichiedente(nmRichiedente);				//NM_RICHIEDENTE
			if(scadenza != "")
				b.setScadenza(Util.stringToDate(scadenza));	//SCADENZA
			b.setUrl(url);									//URL
			b.setStato("DA_PARSIFICARE");					//STATO
			b.setDtInserimento(new Date());					//DT_INSERIMENTO
	
			bandi.add(b);
			
			//SALVA NEL DB
			//repository.create(b);
			}	
		
		return bandi;
	}
	
	/**
	 * Searches the Ban body, trying to find the Ban Object
	 * 
	 * @param ban	html of the whole Ban page
	 */
	public static void parseBan(Bando ban)
	{
		String cig = "", oggetto = "";
		int i = 0, patternLength = PropertiesManager.BAN_OBJ_PATTERNS.length;
		Document doc = Jsoup.parseBodyFragment(ban.getTesto());
		Element mainContent = doc.body();
		Element divBando = mainContent.getElementsByClass(PropertiesManager.BAN_DIVCLASS).get(0);
		String testoBando = divBando.text();

		//aggiorna il testo del bando con quello vero (prima era tutto l'html)
		ban.setTesto(testoBando);		
		
		//estrae CIG, se non e' gia' presente
		if(ban.getCig() == null)
		{
			cig = Util.tryGetCig(testoBando);
			if(!cig.equals(""))
			{
				ban.setCig(cig);
			}
		}

		//estrae OGGETTO
		try
		{
			//prova diversi "pattern", finche' non trova un oggetto valido o finche' non li ha provati tutti
			while((oggetto == "") && (i < patternLength))
			{
				oggetto = Util.tryGetObject(testoBando, PropertiesManager.BAN_OBJ_PATTERNS[i]);
				i++;
			}
			//se ha finito tutti i pattern ma ancora non ha trovato niente, prova a cercare ne titolo
			if(oggetto == "")
			{
				oggetto = Util.tryGetObjectTitle(testoBando);
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
				ban.setOggetto(oggetto);
			}
			
			ban.setStato("PARSIFICATO");
		}
		
		return;
	}
	

	/*		FUNZIONE COMPLESSA, DIVIDE IN VARI CASI 	*/
	/*public static void parseBan(Bando ban)
	{
		String cig = "", oggetto = "", end = "";
		int index = 0, offset = 0;
		boolean strutturato = false;
		char current;
		Document doc = Jsoup.parseBodyFragment(ban.getTesto());
		Element mainContent = doc.body();
		Element divBando = mainContent.getElementsByClass(PropertiesManager.BAN_DIVCLASS).get(0);
		String testoBandoOriginale = divBando.text();
		String testoBandoToLow = testoBandoOriginale.toLowerCase();

		//aggiorna il testo del bando con quello vero (prima era tutto l'html)
		ban.setTesto(testoBandoOriginale);		
		
		//estrae CIG, se non e' gia' presente
		if(ban.getCig() == null)
		{
			cig = Util.tryGetCig(testoBandoOriginale);
			if(!cig.equals(""))
			{
				ban.setCig(cig);
			}
		}
		
		//estrae OGGETTO
		try
		{
			//prova a cercare la sezione 2, con diverse formattazioni
			index = testoBandoToLow.indexOf("sezione ii");
			if(index == -1)
			{
				index = testoBandoToLow.indexOf("sezione 2");
				if(index == -1)
				{
					strutturato = false;
				}
				else
				{
					strutturato = true;
					offset = 9;
				}
			}
			else
			{
				strutturato = true;
				offset = 10;
			}
			
			if(strutturato)
			{
				//cerca sezione II.1.2) Oggetto (partendo da sezione 2 precedentemente trovata)
				index = testoBandoToLow.indexOf("ii.1.2)", index);
				if(index == -1)
				{
					//se non trova la sotto-sezione, cerca "oggetto" in tutta la sezione 2, e si ferma a Sezione 3
					index += offset;
					current = testoBandoOriginale.charAt(index++);
					end = oggetto.trim();
					while((! end.contains("Sezione3")) && (! end.contains("SezioneIII:"))
							&& (! end.contains("Sezione1")) && (! end.contains("SezioneI"))
							&& (! end.contains("Sezione2")) && (! end.contains("Sezione2")))
					{
						oggetto += current;
						current = testoBandoOriginale.charAt(index++);
						end = oggetto.trim();
					}
					//poi analizza l'oggetto per togliere SEZIONE, DESCRIZIONE / OGGETTO...
					oggetto = Util.removeUseless(oggetto);
				}
				else
				{
					//se la trova, salta "II.1.2)" e salva tutta la sotto-sezione
					index += 7;
					current = testoBandoOriginale.charAt(index++);
					end = oggetto.trim();
					//si ferma quando trova la prossima sotto-sezione
					while((! end.contains("III.")) && (! end.contains("SezioneIII")) && (! end.contains("Sezione3"))
							&& (! end.contains("II.")) && (! end.contains("SezioneII")) && (! end.contains("Sezione2")))
					{
						oggetto += current;
						current = testoBandoOriginale.charAt(index++);
						end = oggetto.trim();
					}
					//poi analizza l'oggetto per togliere SEZIONE, DESCRIZIONE / OGGETTO...
					oggetto = Util.removeUseless(oggetto);

				}
			}
			else
			{
				// QUA INTERVIENE WEKA: LEGGI TUTTE LE OCCORRENZE DI BANDO, MA NON TUTTE SONO GIUSTE 
				//se non riesce a trovare la sezione allora il bando non e' strutturato:
				//prova con indexOf oggetto
				index = 0;
				offset = 0;
				oggetto = "";
				while(index != -1)
				{
					index = testoBandoToLow.indexOf("oggetto dell'appalto", offset);
					if(index == -1)
					{
						index = testoBandoToLow.indexOf("oggetto");
						if(index != -1)
							offset = 7;
					}

					else
					{
						offset = 20;
					}

					// se ha trovato almeno una occorrenza di "oggetto", prova a leggere fino a..... ".\n"
					if(offset != 0)
					{
						boolean probablyEnd = false;
						index += offset;
						current = testoBandoOriginale.charAt(index++);
						while(true)
						{
							oggetto += current;

							if(current == '.')
								probablyEnd = true;
							else if(current == '\n' && probablyEnd)
								break;

							current = testoBandoOriginale.charAt(index++);
						}

						//se WEKA trova l'oggetto valido, finisce; altrimenti riprova con la prossima occorrenza di "oggetto"
						if(BandoValidator.validate(oggetto))
							break;
					}
					// se non ha trovato neanche una volta la parola "oggetto", non puo' fare niente
					else
					{
						return;
					}
					
					offset += index;
				}
			}
			
			if(oggetto != "")
			{
				ban.setOggetto(oggetto);
			}
		}
		catch(Exception e)
		{
			//lascia oggetto a null, ma ha comunque provato a parsificare il bando			
		}
		finally
		{
			ban.setStato("PARSIFICATO");
		}
		
		return;
	}
	*/
}
