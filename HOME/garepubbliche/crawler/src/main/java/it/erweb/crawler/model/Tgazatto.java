package it.erweb.crawler.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the tgazatto database table.
 * 
 */
@Entity
@NamedQuery(name="Tgazatto.findAll", query="SELECT t FROM Tgazatto t")
public class Tgazatto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private String cig;

	@Lob
	private String content;

	@Temporal(TemporalType.TIMESTAMP)
	private Date dateinsert;

	private String idtxt;

	@Lob
	private String oggetto;

	@Lob
	private String oggettoWeka;

	private String url;

	public Tgazatto() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCig() {
		return this.cig;
	}

	public void setCig(String cig) {
		this.cig = cig;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getDateinsert() {
		return this.dateinsert;
	}

	public void setDateinsert(Date dateinsert) {
		this.dateinsert = dateinsert;
	}

	public String getIdtxt() {
		return this.idtxt;
	}

	public void setIdtxt(String idtxt) {
		this.idtxt = idtxt;
	}

	public String getOggetto() {
		return this.oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getOggettoWeka() {
		return this.oggettoWeka;
	}

	public void setOggettoWeka(String oggettoWeka) {
		this.oggettoWeka = oggettoWeka;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}