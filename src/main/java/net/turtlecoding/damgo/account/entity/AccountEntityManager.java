package net.turtlecoding.damgo.account.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

public class AccountEntityManager {

    @PersistenceContext
    private EntityManager entityManager;


}
