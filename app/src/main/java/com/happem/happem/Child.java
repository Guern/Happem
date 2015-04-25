package com.happem.happem;

public class Child
{
	private String id;
	private String indirizzo;
	private String descrizione;
	private String stipendio;
	private String luogoLavoro;
	private String candidaturaCellulare;
	private String email;
	String regione;
	String provincia;
	private String YT;
	 
	public String getEmail(){
		return email;
	}
	
	public void setEmail(String e){
		email=e;
	}
	
	public String getStipendio() {
		return stipendio;
	}

	public void setStipendio(String stipendio) {
		this.stipendio = stipendio;
	}

	public String getLuogoLavoro() {
		return luogoLavoro;
	}

	public void setLuogoLavoro(String luogoLavoro) {
		this.luogoLavoro = luogoLavoro;
	}

	public String getCandidaturaCellulare() {
		return candidaturaCellulare;
	}

	public void setCandidaturaCellulare(String i) {
		this.candidaturaCellulare = i;
	}

	public String getId()
	{
		return id;
	}
	
	public void setId(String i)
	{
		this.id = i;
	}
	
	public String getIndirizzo()
	{
		return indirizzo;
	}
	
	public void setIndirizzo(String text1)
	{
		this.indirizzo = text1;
	}
	
	public String getDescrizione()
	{
		return descrizione;
	}
	
	public void setDescrizione(String text2)
	{
		this.descrizione = text2;
	}
	public void setYT(String st){
		this.YT=st;
	}
	public String getYT(){
		return YT;
	}
}
