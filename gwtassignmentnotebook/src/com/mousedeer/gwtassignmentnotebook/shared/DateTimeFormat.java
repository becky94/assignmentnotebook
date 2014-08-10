package com.mousedeer.gwtassignmentnotebook.shared;

import java.text.ParseException;
import java.util.Date;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.thirdparty.guava.common.annotations.GwtCompatible;
import com.google.gwt.thirdparty.guava.common.annotations.GwtIncompatible;

public abstract class DateTimeFormat
{
    static DateTimeFormat getFormat(String pattern)
    {
        if (GWT.isClient())
            return DateTimeFormatClient.getFormat(pattern);
        else
            return DateTimeFormatServer.getFormat(pattern);
    }
    
    public abstract Date parse(String strDate);

    public abstract String format(Date date);

    @GwtCompatible
    private static class DateTimeFormatClient extends DateTimeFormat
    {
        private com.google.gwt.i18n.client.DateTimeFormat dateTimeFormat;

        protected DateTimeFormatClient(String pattern)
        {
            this.dateTimeFormat = com.google.gwt.i18n.client.DateTimeFormat.getFormat(pattern);
        }

        public static DateTimeFormat getFormat(String pattern)
        {
            return new DateTimeFormatClient(pattern);
        }

        public String format(Date date)
        {
            return dateTimeFormat.format(date);
        }
        
        public  Date parse(String strDate)
        {
            return this.dateTimeFormat.parse(strDate);
        }
    }

    @GwtIncompatible("Server version of the class")
    private static class DateTimeFormatServer extends DateTimeFormat
    {
        private java.text.SimpleDateFormat dateTimeFormat;

        protected DateTimeFormatServer(String pattern)
        {
            this.dateTimeFormat = new java.text.SimpleDateFormat(pattern);
        }

        public static DateTimeFormat getFormat(String pattern)
        {
            return new DateTimeFormatServer(pattern);
        }

        public  Date parse(String strDate)
        {
            try {
				return this.dateTimeFormat.parse(strDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
        }

		@Override
		public String format(Date date) {
			return this.dateTimeFormat.format(date);
		}

    }
}