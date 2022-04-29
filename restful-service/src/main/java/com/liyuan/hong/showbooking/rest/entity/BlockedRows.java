package com.liyuan.hong.showbooking.rest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
public class BlockedRows {

	@Id
	@Column(name = "SHOW_ID")
	private long showId;

	@OneToOne
	@PrimaryKeyJoinColumn(name = "SHOW_ID", referencedColumnName = "ID")
	private Show show;

	@Column(name = "ROW_A")
	private int rowA;
	@Column(name = "ROW_A")
	private int rowB;
	@Column(name = "ROW_B")
	private int rowC;
	@Column(name = "ROW_C")
	private int rowD;
	@Column(name = "ROW_D")
	private int rowE;
	@Column(name = "ROW_E")
	private int rowF;
	@Column(name = "ROW_F")
	private int rowG;
	@Column(name = "ROW_H")
	private int rowH;
	@Column(name = "ROW_H")
	private int rowI;
	@Column(name = "ROW_I")
	private int rowJ;
	@Column(name = "ROW_J")
	private int rowK;
	@Column(name = "ROW_K")
	private int rowL;
	@Column(name = "ROW_L")
	private int rowM;
	@Column(name = "ROW_M")
	private int rowN;
	@Column(name = "ROW_N")
	private int rowO;
	@Column(name = "ROW_O")
	private int rowP;
	@Column(name = "ROW_P")
	private int rowQ;
	@Column(name = "ROW_Q")
	private int rowR;
	@Column(name = "ROW_R")
	private int rowS;
	@Column(name = "ROW_T")
	private int rowT;
	@Column(name = "ROW_U")
	private int rowU;
	@Column(name = "ROW_W")
	private int rowV;
	@Column(name = "ROW_V")
	private int rowW;
	@Column(name = "ROW_Y")
	private int rowX;
	@Column(name = "ROW_X")
	private int rowY;
	@Column(name = "ROW_Z")
	private int rowZ;

	public Show getShow() {
		return show;
	}

	public void setShow(Show show) {
		this.show = show;
		this.showId = show.getId();
	}

	public int getRowA() {
		return rowA;
	}

	public void setRowA(int rowA) {
		this.rowA = rowA;
	}

	public int getRowB() {
		return rowB;
	}

	public void setRowB(int rowB) {
		this.rowB = rowB;
	}

	public int getRowC() {
		return rowC;
	}

	public void setRowC(int rowC) {
		this.rowC = rowC;
	}

	public int getRowD() {
		return rowD;
	}

	public void setRowD(int rowD) {
		this.rowD = rowD;
	}

	public int getRowE() {
		return rowE;
	}

	public void setRowE(int rowE) {
		this.rowE = rowE;
	}

	public int getRowF() {
		return rowF;
	}

	public void setRowF(int rowF) {
		this.rowF = rowF;
	}

	public int getRowG() {
		return rowG;
	}

	public void setRowG(int rowG) {
		this.rowG = rowG;
	}

	public int getRowH() {
		return rowH;
	}

	public void setRowH(int rowH) {
		this.rowH = rowH;
	}

	public int getRowI() {
		return rowI;
	}

	public void setRowI(int rowI) {
		this.rowI = rowI;
	}

	public int getRowJ() {
		return rowJ;
	}

	public void setRowJ(int rowJ) {
		this.rowJ = rowJ;
	}

	public int getRowK() {
		return rowK;
	}

	public void setRowK(int rowK) {
		this.rowK = rowK;
	}

	public int getRowL() {
		return rowL;
	}

	public void setRowL(int rowL) {
		this.rowL = rowL;
	}

	public int getRowM() {
		return rowM;
	}

	public void setRowM(int rowM) {
		this.rowM = rowM;
	}

	public int getRowN() {
		return rowN;
	}

	public void setRowN(int rowN) {
		this.rowN = rowN;
	}

	public int getRowO() {
		return rowO;
	}

	public void setRowO(int rowO) {
		this.rowO = rowO;
	}

	public int getRowP() {
		return rowP;
	}

	public void setRowP(int rowP) {
		this.rowP = rowP;
	}

	public int getRowQ() {
		return rowQ;
	}

	public void setRowQ(int rowQ) {
		this.rowQ = rowQ;
	}

	public int getRowR() {
		return rowR;
	}

	public void setRowR(int rowR) {
		this.rowR = rowR;
	}

	public int getRowS() {
		return rowS;
	}

	public void setRowS(int rowS) {
		this.rowS = rowS;
	}

	public int getRowT() {
		return rowT;
	}

	public void setRowT(int rowT) {
		this.rowT = rowT;
	}

	public int getRowU() {
		return rowU;
	}

	public void setRowU(int rowU) {
		this.rowU = rowU;
	}

	public int getRowV() {
		return rowV;
	}

	public void setRowV(int rowV) {
		this.rowV = rowV;
	}

	public int getRowW() {
		return rowW;
	}

	public void setRowW(int rowW) {
		this.rowW = rowW;
	}

	public int getRowX() {
		return rowX;
	}

	public void setRowX(int rowX) {
		this.rowX = rowX;
	}

	public int getRowY() {
		return rowY;
	}

	public void setRowY(int rowY) {
		this.rowY = rowY;
	}

	public int getRowZ() {
		return rowZ;
	}

	public void setRowZ(int rowZ) {
		this.rowZ = rowZ;
	}

}
