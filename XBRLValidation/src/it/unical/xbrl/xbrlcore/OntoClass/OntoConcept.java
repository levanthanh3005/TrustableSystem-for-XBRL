/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.xbrl.xbrlcore.OntoClass;

import it.unical.xbrl.xbrlcore.xbrlcore.taxonomy.Concept;

/**
 *
 * @author Admin
 */
public class OntoConcept extends ObjectEsd {
    private String id;
    private String name;
    private String namespaceprefix;
    private String namespaceuri;

    @Override
    public String getContext() {
        try {
            return "  : ( id :\"" + id + "\" name : \"" + name + "\" namespaceprefix : \"" + namespaceprefix + "\" namespaceuri : \"" + namespaceuri + "\")";
        } catch (Exception e) {
            return ": concept NULL";
        }
    }

    @Override
    public String getClassType() {
        return "concept";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespaceprefix() {
        return namespaceprefix;
    }

    public void setNamespaceprefix(String namespaceprefix) {
        this.namespaceprefix = namespaceprefix;
    }

    public String getNamespaceuri() {
        return namespaceuri;
    }

    public void setNamespaceuri(String namespaceuri) {
        this.namespaceuri = namespaceuri;
    }
    public boolean isCoverConcept(Concept c) {
        return (id.equals(c.getId()) && name.equals(c.getName()) && namespaceprefix.equals(c.getNamespace().getPrefix()) && namespaceuri.equals(c.getNamespace().getURI()));
    }

    public OntoConcept(String id, String name, String namespaceprefix, String namespaceuri) {
        this.id = id;
        this.name = name;
        this.namespaceprefix = namespaceprefix;
        this.namespaceuri = namespaceuri;
    }

    public OntoConcept() {
    }
    
}
