// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.github.lbroudoux.roo.addon.cloneable.domain;

import com.github.lbroudoux.roo.addon.cloneable.domain.Tweet;
import java.util.List;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

privileged aspect Tweet_Roo_Cloneable {
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Tweet Tweet.cloneReference;
    
    public Tweet Tweet.getCloneReference() {
        return this.cloneReference;
    }
    
    public List<Tweet> Tweet.getClones() {
        return entityManager().createQuery("SELECT o FROM Tweet o WHERE o.cloneReference=:reference").setParameter("reference", this).getResultList();
    }
    
    public Tweet Tweet.createClone() {
        Tweet clone = new Tweet();
        clone.cloneReference = this;
        clone.author = this.author;
        clone.content = this.content;
        clone.original = this.original;
        clone.persist();
        return clone;
    }
    
}