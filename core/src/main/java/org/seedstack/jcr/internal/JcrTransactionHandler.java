package org.seedstack.jcr.internal;

import javax.transaction.UserTransaction;

import org.seedstack.seed.transaction.spi.TransactionHandler;
import org.seedstack.seed.transaction.spi.TransactionMetadata;

public class JcrTransactionHandler implements TransactionHandler<UserTransaction>{

    @Override
    public void doInitialize(TransactionMetadata transactionMetadata) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public UserTransaction doCreateTransaction() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void doJoinGlobalTransaction() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void doBeginTransaction(UserTransaction currentTransaction) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void doCommitTransaction(UserTransaction currentTransaction) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void doMarkTransactionAsRollbackOnly(UserTransaction currentTransaction) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void doRollbackTransaction(UserTransaction currentTransaction) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void doReleaseTransaction(UserTransaction currentTransaction) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void doCleanup() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public UserTransaction getCurrentTransaction() {
        // TODO Auto-generated method stub
        return null;
    }



}
