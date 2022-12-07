package com.contentree.interna.user.entity;

import lombok.Getter;

@Getter
public enum Role {
	ROLE_USER, ROLE_JOINS, ROLE_ADMIN;

	public String toString() {
		return this.name();
	}
}