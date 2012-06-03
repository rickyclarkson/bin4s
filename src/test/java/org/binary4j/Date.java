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

    static XFunction<Pair<Pair<Integer, Integer>, Integer>, Date> xmap = new XFunction<Pair<Pair<Integer, Integer>, Integer>, Date>()
    {
        @Override
        public Date apply(Pair<Pair<Integer, Integer>, Integer> yearMonthDay) {
            return new Date(yearMonthDay._1._1, yearMonthDay._1._2, yearMonthDay._2);
        }

        @Override
        public Pair<Pair<Integer, Integer>, Integer> unapply(Date date) {
            return Pair.pair(Pair.pair(date.year, date.month), date.day);
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
