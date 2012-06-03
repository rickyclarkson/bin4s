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

    static XFunction3<String, String, Date, Person> xmap = new XFunction3<String, String, Date, Person>()
    {
        public Person apply(String name, String address, Date dateOfBirth)
        {
            return new Person(name, address, dateOfBirth);
        }

        public Tuple3<String, String, Date> unapply3(Person person)
        {
            return Tuple3.tuple3(person.name, person.address, person.dateOfBirth);
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
