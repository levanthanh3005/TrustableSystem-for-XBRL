package it.unical.xbrl.xbrlcore.xbrlcore.exception;

/**
 * Superclass of all exceptions generated by the xbrlcore classes. <br/><br/>
 * 
 * @author Daniel Hamm
 */
public class XBRLException extends XBRLCoreException {

    static final long serialVersionUID = 4095277146083937776L;
	
    public XBRLException(String message) {
        super(message);
    }
}
