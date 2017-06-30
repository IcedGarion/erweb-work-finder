package it.erweb.crawler.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * The persistent class for the bando database table.
 */
@Entity
@NamedQuery(name = "Bando.findAll", query = "SELECT b FROM Bando b")
public class Bando extends AbstractModel implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "CD_BANDO")
	private String cdBando;

	@Column(name = "CD_ESTERNO")
	private String cdEsterno;

	private String cig;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_INSERIMENTO")
	private Date dtInserimento;

	@Column(name = "NM_RICHIEDENTE")
	private String nmRichiedente;

	private String oggetto;

	@Temporal(TemporalType.DATE)
	private Date scadenza;

	private String stato;

	@Lob
	private String testo;

	private String tipo;

	private String tiporichiedente;

	private String url;

	// bi-directional many-to-one association to Pubblicazione
	@ManyToOne
	@JoinColumn(name = "CD_PUBBLICAZIONE")
	private Pubblicazione pubblicazione;

	public Bando()
	{
	}

	public String getCdBando()
	{
		return this.cdBando;
	}

	public void setCdBando(String cdBando)
	{
		this.cdBando = cdBando;
	}

	public String getCdEsterno()
	{
		return this.cdEsterno;
	}

	public void setCdEsterno(String cdEsterno)
	{
		this.cdEsterno = cdEsterno;
	}

	public String getCig()
	{
		return this.cig;
	}

	public void setCig(String cig)
	{
		this.cig = cig;
	}

	public Date getDtInserimento()
	{
		return this.dtInserimento;
	}

	public void setDtInserimento(Date dtInserimento)
	{
		this.dtInserimento = dtInserimento;
	}

	public String getNmRichiedente()
	{
		return this.nmRichiedente;
	}

	public void setNmRichiedente(String nmRichiedente)
	{
		this.nmRichiedente = nmRichiedente;
	}

	public String getOggetto()
	{
		return this.oggetto;
	}

	public void setOggetto(String oggetto)
	{
		this.oggetto = oggetto;
	}

	public Date getScadenza()
	{
		return this.scadenza;
	}

	public void setScadenza(Date scadenza)
	{
		this.scadenza = scadenza;
	}

	public String getStato()
	{
		return this.stato;
	}

	public void setStato(String stato)
	{
		this.stato = stato;
	}

	public String getTesto()
	{
		return this.testo;
	}

	public void setTesto(String testo)
	{
		this.testo = testo;
	}

	public String getTipo()
	{
		return this.tipo;
	}

	public void setTipo(String tipo)
	{
		this.tipo = tipo;
	}

	public String getTiporichiedente()
	{
		return this.tiporichiedente;
	}

	public void setTiporichiedente(String tiporichiedente)
	{
		this.tiporichiedente = tiporichiedente;
	}

	public String getUrl()
	{
		return this.url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public Pubblicazione getPubblicazione()
	{
		return this.pubblicazione;
	}

	public void setPubblicazione(Pubblicazione pubblicazione)
	{
		this.pubblicazione = pubblicazione;
	}

	@Override
	public String toString()
	{
		return cdBando + ", " + cdEsterno + ", " + pubblicazione + ", " + cig + ", " + tipo + ", " + tiporichiedente
				 + ", " + nmRichiedente + ", " + scadenza + ", " + oggetto + ", " + testo + ", " + url + ", " + stato;
	}

}