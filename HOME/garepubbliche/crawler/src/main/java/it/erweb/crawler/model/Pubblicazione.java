package it.erweb.crawler.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the pubblicazione database table.
 * 
 */
@Entity
@NamedQuery(name = "Pubblicazione.findAll", query = "SELECT p FROM Pubblicazione p")
public class Pubblicazione extends AbstractModel implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "CD_PUBBLICAZIONE")
	private String cdPubblicazione;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_INSERIMENTO")
	private Date dtInserimento;

	@Column(name = "NM_PUBBLICAZIONE")
	private int nmPubblicazione;

	private String stato;

	private String url;

	// bi-directional many-to-one association to Bando
	@OneToMany(mappedBy = "pubblicazione")
	private List<Bando> bandos;

	public Pubblicazione()
	{
	}

	public String getCdPubblicazione()
	{
		return this.cdPubblicazione;
	}

	public void setCdPubblicazione(String cdPubblicazione)
	{
		this.cdPubblicazione = cdPubblicazione;
	}

	public Date getDtInserimento()
	{
		return this.dtInserimento;
	}

	public void setDtInserimento(Date dtInserimento)
	{
		this.dtInserimento = dtInserimento;
	}

	public int getNmPubblicazione()
	{
		return this.nmPubblicazione;
	}

	public void setNmPubblicazione(int nmPubblicazione)
	{
		this.nmPubblicazione = nmPubblicazione;
	}

	public String getStato()
	{
		return this.stato;
	}

	public void setStato(String stato)
	{
		this.stato = stato;
	}

	public String getUrl()
	{
		return this.url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public List<Bando> getBandos()
	{
		return this.bandos;
	}

	public void setBandos(List<Bando> bandos)
	{
		this.bandos = bandos;
	}

	public Bando addBando(Bando bando)
	{
		getBandos().add(bando);
		bando.setPubblicazione(this);

		return bando;
	}

	public Bando removeBando(Bando bando)
	{
		getBandos().remove(bando);
		bando.setPubblicazione(null);

		return bando;
	}

	@Override
	public String toString()
	{
		return cdPubblicazione + ", " + dtInserimento + ", " + nmPubblicazione + ", " + stato + ", " + url;
	}

}