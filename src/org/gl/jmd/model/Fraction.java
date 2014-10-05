package org.gl.jmd.model;

import java.io.Serializable;

public class Fraction implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int numerateur = 0;
	
	private int denominateur = 0;
	
	public Fraction(int numerateur, int denominateur) {
		this.numerateur = numerateur;
		this.denominateur = denominateur;
	}

	/* Getters. */
	
	public int getNumerateur() {
		return this.numerateur;
	}

	public int getDenominateur() {
		return this.denominateur;
	}
	
	/* Setters. */

	public void setNumerateur(int numerateur) {
		this.numerateur = numerateur;
	}

	public void setDenominateur(int denominateur) {
		this.denominateur = denominateur;
	}
	
	/* Autres. */
	
	public String get() {
		if ((this.numerateur > 0) && (this.denominateur > 0)) {
			return this.numerateur + "/" + this.denominateur;
		} else {
			return "0";
		}
	}
	
	public Fraction getOppose() {
		return new Fraction(this.denominateur - this.numerateur, this.denominateur);
	}
	
	public Fraction sum(Fraction f) throws NullPointerException {
		if ((this.denominateur == this.numerateur) && (this.denominateur == 0)) {
			return new Fraction(f.getNumerateur(), f.getDenominateur());
		} else {		
			if (this.denominateur != f.getDenominateur()) {
				return new Fraction(((this.numerateur * f.getDenominateur()) + f.getNumerateur()), (this.denominateur * f.getDenominateur()));
			} else {
				return new Fraction((this.numerateur + f.getNumerateur()), this.denominateur);
			}
		}
	}
	
	public double multiplyByNote(double note) {
		return (this.numerateur * note) / this.denominateur;
	}
}