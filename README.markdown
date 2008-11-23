Binary Reading and Writing Combinators for Java
-----------------------------------------------

Binary4J is a combinator library for reading and writing arbitrary file and stream formats.

Why does it exist?
------------------

In my work I needed to parse and generate some proprietary binary formats, which were completely undocumented.  I had some C code that generated them and some Java that parsed them.  I found both the C and the Java tricky to understand, but persevered and hacked together valid parsers and generators.

I thought that I should try not to add a 3rd implementation that's difficult to understand, and I should try not to repeat the format in both the parsing and generating code.  So I came up with binary4j.  Here's the main principle:

If you have a Format&lt;T&gt; (which can read and write Ts), you can combine it with a Format&lt;U&gt; to form a Format2&lt;T, U&gt;.  Obviously combining a few gets unwieldy and lacks information about what each type parameter means, so you can describe how to get from a T and a U to a V, and back again, to create a Format&lt;V&gt;.  There are some less abstract examples of this in the next section.

How do I use it?
----------------

As an example, we'll consider a Person class.  A person has a String name, String address and Date dateOfBirth.
A Date has an int year, int month and int day.  Format.string and Format.integer are provided in Binary4J.  We can combine these to produce a Format&lt;Person&gt;.  First let's tackle Format&lt;Date&gt;.

Format.integer.andThen(Format.integer) returns a Format2&lt;Integer, Integer&gt;.  But Date has 3 ints..
Format.integer.andThen(Format.integer).andThen(Format.integer) returns a Format3&lt;Integer, Integer, Integer&gt;.  Handy, but not quite a Format&lt;Date&gt;.  To make a Format&lt;Date&gt; from it we need to tell Binary4J about a way of converting between 3 Integers and Dates, in both directions (consider that a Format can read and write).  Luckily a Format3 has a map method that takes an XFunction3, which describes these conversions.

    Format&lt;Date&gt; dateFormat = Format.integer.andThen(Format.integer).andThen(Format.integer).map(Date.xFunction);

So similarly we can combine this with Format.string to create a Format&lt;Person&gt;:

    Format&lt;Person&gt; personFormat = Format.person.andThen(Format.person).andThen(dateFormat).andThen(dateFormat).map(Format.xFunction);

Then to get a ByteBuffer containing that data, we can say: ByteBuffer personData = personFormat.apply(somePerson);
To read a ByteBuffer into a Person we can say: Person person = personFormat.unapply(buffer);

For this particular case, it's possible that the actual number of lines has increased between the 'traditional' solution and this one.

A more realistic example, perhaps, is a block of bytes preceded by its length as an int.

    Format2&lt;Integer, byte[]&gt; lengthEncodedBytes = Format.integer.bind(Format.byteArray);

Unfortunately to use lengthEncodedBytes, we have to pass in the length separately to the array, so typical uses would look like:

    ByteBuffer buffer = lengthEncodedBytes.apply(array.length, array);

Clearly it would be better if we could make this a Format&lt;byte[]&gt;.  Luckily we can, by providing an XFunction2 describing the conversion from a byte[] to a Integer-byte[] pair, and vice-versa.

    Format&lt;byte[]&gt; lengthEncodedBytes = Format.integer.bind(Format.byteArray).map(someXFunction2);

What I especially like about this approach is that each part of it is simple, at whatever scale you look.  It took a LOT of work to make the types readable, so the next section explains how Java could have helped but didn't:

How Java Made This Hard
-----------------------

It's a shame that I 'need' to have Format2, Format3, etc.  It's a shame I 'need' Tuple3 and Tuple4.  It's a shame I 'need' Function2, Function3, Function4.

But, lacking tuple support in the language, Format&lt;Pair&lt;Pair&lt;X, Y&gt;, Z&gt;&gt; is considerably harder to read (and write) than Format3&lt;X, Y, Z&gt;.  A better language, or a future Java, might allow Format&lt;(X, Y, Z)&gt;.

If Java had type inference, then sometimes Format&lt;Pair&lt;Pair&lt;X, Y&gt;, Z&gt;&gt; would have been fine, because it would not have actually appeared in user code, e.g.:

    var threeInts = Format.integer.andThen(Format.integer).andThen(Format.integer);
    var dateFormat = threeInts.map(Date.xFunction);

As it is, a user of binary4j is 'punished' for introducing an explaining variable like threeInts above, though binary4j takes great effort to minimise that (using Format3 instead of Format&lt;Pair&lt;Pair..&gt;&gt;.

If Java had support for closures, then the xFunction implementations could have been much much simpler.  In fact, XFunction might not even exist, as it just represents a tuple of (X =&gt; Y, Y =&gt; X).

If you are using binary4j, please let me know, and I'll do what I can to help you.  Other than that, it will evolve as and when I use it, which might be daily or never.