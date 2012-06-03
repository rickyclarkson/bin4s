package org.binary4j;

class Date
{
    int year;
    int month;
    int day;

    public Date(int year, int month, int day)
    {
        this.year = year;
        this.month = month;
        this.day= day;
    }

    static XFunction3<Integer, Integer, Integer, Date> xmap = new XFunction3<Integer, Integer, Integer, Date>()
    {
        public Date apply(Integer year, Integer month, Integer day)
        {
            return new Date(year, month, day);
        }

        public Tuple3<Integer, Integer, Integer> unapply3(Date date)
        {
            return Tuple3.tuple3(date.year, date.month, date.day);
        }
    };

    public String toString()
    {
        return "Date[year="+year+", month="+month+", day="+day+"]";
    }

    public boolean equals(Object other)
    {
        if (other instanceof Date)
        {
            Date o = (Date)other;
            return o.day == day && o.month == month && o.year == year;
        }

        return false;
    }
}
