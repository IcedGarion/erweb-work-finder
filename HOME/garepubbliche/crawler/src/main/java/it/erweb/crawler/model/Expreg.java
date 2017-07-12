package it.erweb.crawler.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The persistent class for the expreg database table.
 * 
 */
@Entity
@NamedQuery(name = "Expreg.findAll", query = "SELECT e FROM Expreg e")
public class Expreg extends AbstractModel implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "CD_EXPREG")
	private String cdExpreg;

	private String expminus;

	private String expplus;

	// bi-directional many-to-one association to Utente
	@ManyToOne
	@JoinColumn(name = "CD_UTENTE")
	private Utente utente;

	public Expreg()
	{
	}

	public String getCdExpreg()
	{
		return this.cdExpreg;
	}

	public void setCdExpreg(String cdExpreg)
	{
		this.cdExpreg = cdExpreg;
	}

	public String getExpminus()
	{
		return this.expminus;
	}

	public void setExpminus(String expminus)
	{
		this.expminus = expminus;
	}

	public String getExpplus()
	{
		return this.expplus;
	}

	public void setExpplus(String expplus)
	{
		this.expplus = expplus;
	}

	public Utente getUtente()
	{
		return this.utente;
	}

	public void setUtente(Utente utente)
	{
		this.utente = utente;
	}

	@Override
	public String toString()
	{
		return this.cdExpreg + ", " + this.utente + ", " + this.expplus + ", " + this.expminus;
	}

}