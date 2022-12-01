package com.contentree.interna.user.entity;

import lombok.Getter;

@Getter
public enum Role {
	USER, JOINS, ADMIN;

	public String toString() {
		return this.name();
	}
}
