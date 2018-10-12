/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.xbrl.xbrlcore.OntoClass;

/**
 *
 * @author Admin
 */
public class OntoEntity extends ObjectEsd{

    private String identifier;
    private String identifierScheme;

    @Override
    public String getContext() {
        return " : entity(identifier:\"" + identifier.toLowerCase() + "\" , " + " identifierscheme: \"" + identifierScheme.toLowerCase() + "\").";
    }

    @Override
    public String getClassType() {
        return "entity";
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setIdentifierScheme(String identifierScheme) {
        this.identifierScheme = identifierScheme;
    }
    
}
