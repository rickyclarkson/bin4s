Binary Reading and Writing Combinators for Scala
-----------------------------------------------

bin4s is a combinator library for reading and writing arbitrary file and stream formats.

Why does it exist?
------------------

In my work I needed to parse and generate some proprietary binary formats, which were completely undocumented.  I had some C code that generated them and some Java that parsed them.  I found both the C and the Java tricky to understand, but persevered and hacked together valid parsers and generators.

I thought that I should try not to add a 3rd implementation that's difficult to understand, and I should try not to repeat the format in both the parsing and generating code.  So I came up with bin4s.  Here's the main principle:

If you have a Format[T] (which can read and write Ts), you can combine it with a Format[U] to form a Format[(T, U)].  Obviously combining a few gets unwieldy and lacks information about what each type parameter means, so you can describe how to get from a T and a U to a V, and back again, to create a Format[V].  There are some less abstract examples of this in the next section.

How do I use it?
----------------

As an example, we'll consider a Person class.  A person has a String name, String address and Date dateOfBirth.
A Date has an int year, int month and int day.  Format.string and Format.integer are provided in Binary4Scala.  We can combine these to produce a Format[Person].  First let's tackle Format[Date].

Format.integer.andThen(Format.integer) returns a Format[(Int, Int)].  But Date has 3 Ints..
Format.integer.andThen(Format.integer).andThen(Format.integer) returns a Format[((Int, Int), Int)];.
Handy, but not quite a Format[Date].  To make a Format[Date] from it we need to tell bin4s about a way of converting between 3 Ints and Dates,
in both directions (consider that a Format can read and write).  Luckily a Format has a map method that takes an XFunction, which describes these conversions.

    val dateFormat: Format[Date] = Format.integer andThen Format.integer andThen Format.integer map Date.xFunction

So similarly we can combine this with Format.string to create a Format[Person]:

    val personFormat: Format[Person] = Format.string andThen Format.string andThen dateFormat map Person.xFunction

Then to get a ByteBuffer containing that data, we can say: val personData = personFormat(somePerson);
To read a ByteBuffer into a Person we can say: val person = personFormat.unapply(buffer);

A more realistic example, perhaps, is a block of bytes preceded by its length as an int.

    val lengthEncodedBytes = Format[(Int, Array[Byte])] = Format.integer bind Format.byteArray

Unfortunately to use lengthEncodedBytes, we have to pass in the length separately to the array, so typical uses would look like:

    val buffer = lengthEncodedBytes(array.length, array)

Clearly it would be better if we could make this a Format[Array[Byte]].  Luckily we can, by providing an XFunction describing the conversion from a Array[Byte] to a (Int, Array[Byte]),
and vice-versa.

    val lengthEncodedBytes: Format[Array[Byte]] = Format.integer bind Format.byteArray map someXFunction2

If you are using bin4s, please let me know, and I'll do what I can to help you.  Other than that, it will evolve as and when I use it, which might be daily or never.
