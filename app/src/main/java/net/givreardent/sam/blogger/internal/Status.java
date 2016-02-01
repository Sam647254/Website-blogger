package net.givreardent.sam.blogger.internal;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sam on 26/12/15.
 */
public class Status implements Serializable {
    public int ID;
    public String content_en;
    public String content_fr;
    public Date updated;
}
