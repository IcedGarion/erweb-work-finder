# GarePubbliche

L'obiettivo del progetto è di raccogliere e memorizzare i bandi delle gare pubbliche pubblicati dalla Gazzetta
Ufficiale, per poi notificare gli utenti registrati al sistema della presenza degli stessi, appena sono disponibili; gli utenti
possono esprimere delle preferenze sui bandi a cui sono interessati e sono notificati soltanto se i bandi rispecchiano le
preferenze. <br/><br/>
La Gazzetta è un documento ufficiale pubblicato dalla Republica Italiana con cadenza al massimo giornaliera e viene
esposta in una pagina web al seguente indirizzo: http://www.gazzettaufficiale.it/ (Home Page della Gazzetta). Le sue
pubblicazioni sono identificate da data e un numero, che si azzera con la fine dell'anno. Una pubblicazione espone, oltre
a decreti, sentenze e regolamenti, diverse decine di bandi di vario tipo, offerti da enti anche molto diversi quali Regioni,
Province, Comuni, Ministeri... <br/><br/>
I bandi rappresentano delle gare pubbliche, cioè appalti di lavori o incarichi pubblici offerti dallo Stato e liberamente
fruibili da privati o associazioni, previo concorso. Le informazioni principali che riguardano i bandi sono CIG (Codice
Identificativo Gara) e oggetto della gara.
Per ogni nuovo bando disponibile si memorizzano in modo persistente queste informazioni, per poi notificare gli utenti
interessati.<br><br/>
Il sistema prevede un meccanismo di registrazione dei nuovi utenti con relativo username e password. Ogni utente
imposta alcuni parametri che rappresentano le sue preferenze riguardo a certi bandi.
Gli utenti possono definire gli argomenti che interessano tramite alcune espressioni da ricercare nel testo del bando. Si
definiscono le espressioni che devono comparire nel testo e quelle che invece non devono essere presenti; se poi il
bando contiene una o più espressioni della prima categoria ma nessuna della seconda, allora l'utente è notificato.
Quando dei nuovi bandi sono esposti sulla Gazzetta sono scaricati e memorizzati; il sistema sceglie quali sono gli utenti
registrati interessati ai nuovi bandi: analizza il testo del bando e cerca se sono presenti o no le parole chiave definite
dagli utenti, poi li notifica.
Le notifiche avvengono tramite servizi Internet: un sito web e eventualmente posta elettronica, se l'utente lo ha
specificato. In fase di registrazione l'utente definisce pertanto anche se desidera ricevere le notifiche per e-mail, oltre
che visualizzarle sul sito web.
