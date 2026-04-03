package com.nunclear.escritores.entity;

public enum Role {
    LECTOR,      // Reader - can only read stories
    USUARIO,     // User - can create and edit own stories
    MODERADOR,   // Moderator - can moderate content
    ADMINISTRADOR // Administrator - full access
}
