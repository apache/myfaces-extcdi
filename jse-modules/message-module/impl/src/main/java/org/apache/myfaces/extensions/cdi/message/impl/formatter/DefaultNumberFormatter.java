/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.extensions.cdi.message.impl.formatter;

import org.apache.myfaces.extensions.cdi.message.api.Formatter;
import org.apache.myfaces.extensions.cdi.message.api.GenericConfig;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author Manfred Geiler
 * @author Gerhard Petracek
 */
class DefaultNumberFormatter implements Formatter<Number>
{
    private static final long serialVersionUID = 1639250543559140704L;

    private Character groupingSeparator;
    private Character decimalSeparator;
    private Integer minimumFractionDigits;
    private Integer minimumIntegerDigits;

    DefaultNumberFormatter()
    {
    }

    public boolean isResponsibleFor(Class<?> type)
    {
        return Number.class.isAssignableFrom(type);
    }

    public boolean isStateless()
    {
        return false;
    }

    public String format(MessageContext messageContext, Number valueToFormat)
    {
        GenericConfig formatterConfig = messageContext.config().getFormatterFactory()
                .findFormatterConfig(Number.class, messageContext.getLocale());

        if (formatterConfig != null)
        {
            useCustomConfig(formatterConfig);
        }
        else
        {
            resetConfig();
        }

        Locale locale = messageContext.config().getLocaleResolver().getLocale();

        if (valueToFormat instanceof BigDecimal || valueToFormat instanceof Double || valueToFormat instanceof Float)
        {
            return getDecimalFormat(locale).format(valueToFormat);
        }
        else
        {
            return getNumberFormat(locale).format(valueToFormat);
        }
    }

    private void resetConfig()
    {
        this.groupingSeparator = null;
        this.decimalSeparator = null;
        this.minimumFractionDigits = null;
        this.minimumIntegerDigits = null;
    }

    private void useCustomConfig(GenericConfig formatterConfig)
    {
        if (formatterConfig.containsProperty(NumberFormatterConfigKeys.GROUPING_SEPARATOR_KEY))
        {
            this.groupingSeparator = formatterConfig
                    .getProperty(NumberFormatterConfigKeys.GROUPING_SEPARATOR_KEY, String.class).charAt(0);
        }

        if (formatterConfig.containsProperty(NumberFormatterConfigKeys.DECIMAL_SEPARATOR_KEY))
        {
            this.decimalSeparator = formatterConfig
                    .getProperty(NumberFormatterConfigKeys.DECIMAL_SEPARATOR_KEY, String.class).charAt(0);
        }

        if (formatterConfig.containsProperty(NumberFormatterConfigKeys.MINIMUM_FRACTION_DIGITS_KEY))
        {
            this.minimumFractionDigits = formatterConfig
                    .getProperty(NumberFormatterConfigKeys.MINIMUM_FRACTION_DIGITS_KEY, Integer.class);
        }

        if (formatterConfig.containsProperty(NumberFormatterConfigKeys.MINIMUM_INTEGER_DIGITS_KEY))
        {
            this.minimumIntegerDigits = formatterConfig
                    .getProperty(NumberFormatterConfigKeys.MINIMUM_INTEGER_DIGITS_KEY, Integer.class);
        }
    }

    private NumberFormat getNumberFormat(Locale locale)
    {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);

        if (this.groupingSeparator != null)
        {
            symbols.setGroupingSeparator(this.groupingSeparator);
        }
        if (this.decimalSeparator != null)
        {
            symbols.setDecimalSeparator(this.decimalSeparator);
        }
        return new DecimalFormat("", symbols);
    }

    private NumberFormat getDecimalFormat(Locale locale)
    {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);

        if (this.groupingSeparator != null)
        {
            symbols.setGroupingSeparator(this.groupingSeparator);
        }
        if (this.decimalSeparator != null)
        {
            symbols.setDecimalSeparator(this.decimalSeparator);
        }

        DecimalFormat format = new DecimalFormat("", symbols);

        if (this.minimumFractionDigits != null)
        {
            format.setMinimumFractionDigits(this.minimumFractionDigits);
        }
        if (this.minimumIntegerDigits != null)
        {
            format.setMinimumIntegerDigits(this.minimumIntegerDigits);//for 0.**
        }
        return format;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof DefaultNumberFormatter))
        {
            return false;
        }

        DefaultNumberFormatter that = (DefaultNumberFormatter) o;

        if (decimalSeparator != null ? !decimalSeparator.equals(that.decimalSeparator) : that.decimalSeparator
                != null)
        {
            return false;
        }
        if (groupingSeparator != null ? !groupingSeparator.equals(that.groupingSeparator) : that.groupingSeparator
                != null)
        {
            return false;
        }
        if (minimumFractionDigits != null ?
                !minimumFractionDigits.equals(that.minimumFractionDigits) : that.minimumFractionDigits != null)
        {
            return false;
        }
        if (minimumIntegerDigits != null ?
                !minimumIntegerDigits.equals(that.minimumIntegerDigits) : that.minimumIntegerDigits != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = groupingSeparator != null ? groupingSeparator.hashCode() : 0;
        result = 31 * result + (decimalSeparator != null ? decimalSeparator.hashCode() : 0);
        result = 31 * result + (minimumFractionDigits != null ? minimumFractionDigits.hashCode() : 0);
        result = 31 * result + (minimumIntegerDigits != null ? minimumIntegerDigits.hashCode() : 0);
        return result;
    }
}
