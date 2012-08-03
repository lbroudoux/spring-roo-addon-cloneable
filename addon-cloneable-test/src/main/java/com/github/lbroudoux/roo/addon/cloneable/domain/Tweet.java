package com.github.lbroudoux.roo.addon.cloneable.domain;

import java.util.Set;

import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.github.lbroudoux.roo.addon.cloneable.FetchType;
import com.github.lbroudoux.roo.addon.cloneable.RooCloneable;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooCloneable(fetch = FetchType.lazy)
public class Tweet {
   
   @NotNull
   String author;
   
   @NotNull
   @Size(max=140)
   String content;
   
   @OneToMany(mappedBy = "original")
   Set<Tweet> retweets;
   
   @ManyToOne
   Tweet original;
}
