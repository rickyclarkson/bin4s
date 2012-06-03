package org.binary4j;

final class Person
{
    String name;
    String address;
    Date dateOfBirth;

    Person(String name, String address, Date dateOfBirth)
    {
        this.name=name;
        this.address=address;
        this.dateOfBirth=dateOfBirth;
    }

    static XFunction<Pair<Pair<String, String>, Date>, Person> xmap = new XFunction<Pair<Pair<String, String>, Date>, Person>()
    {
        public Person apply(Pair<Pair<String, String>, Date> nameAddressDateOfBirth)
        {
            return new Person(nameAddressDateOfBirth._1._1, nameAddressDateOfBirth._1._2, nameAddressDateOfBirth._2);
        }

        @Override
        public Pair<Pair<String, String>, Date> unapply(Person person) {
            return Pair.pair(Pair.pair(person.name, person.address), person.dateOfBirth);
        }
    };

    public String toString()
    {
        return "Person(name="+name+", address="+address+", dateOfBirth="+dateOfBirth+")";
    }

    public boolean equals(Object other)
    {
        if (other instanceof Person)
        {
            Person p = (Person)other;
            return p.name.equals(name) && p.address.equals(address) && p.dateOfBirth.equals(dateOfBirth);
        }

        return false;
    }
}
