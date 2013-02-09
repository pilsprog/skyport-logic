#MARKDOWN HOWTO
***
##WHAT IS MARKDOWN?
Markdown is an easy-to-use formatting syntax, created by [John Gruber of Daring Fireball](http://daringfireball.net/projects/markdown/syntax).
The goal of markdown is to be as *easy to read and write as possible*. A document formatted with
markdown should still be readable without actually formatting it. The function of markdown is
therefore different from HTML in its basic purpose; writing text, not publishing a web-page.

###HOW DOES IT WORK?
Gruber claims that the foremost source of inspiration for Markdown is emails. Therefore the syntax
is comprised of punctuation characters and symbols, easily available on a standard keyboard.
Markdown also support inline HTML if you should find it lacking. Block-level HTML elements (\<div>,
\<table>, \<p>, etc) must be separated with blank lines, and no indentation on start and end tags
of the block. Also note that the Markdown syntax is disabled within HTML tags.

##COMMON SYNTAX
###HEADERS
Markdown supports Setext and atx headers.
####ATX
Atx-style headers have six layers. To create a header with the desired level, just prefix it with 
1-6 hash characters:
	#H1
	##H2
	###H3
	######H6
You can also add "end" hash characters, but this is purely cosmetic and it doesnt even have to
match the number of hashes used to create the header.
	#H1#
	##H2#
	###H3############

####SETEXT
Setext-style headers have two layers. To create a H1 you simply underline the text with
some equal signs.
	H1
	=====
H2 is the same, but using dashes. Note that the number of dashes or equal signs is not important.
	H2
	--------------------

###BOLD AND EMPHASIS
Bold text is simply triggered by wrapping the desired text with two asterisks on both sides.
	**BOLD TEXT**
Emphasis is the same, but with only one asterisk.
	*EMPHASIZED TEXT*

###BLOCKQUOTES
Markdown's blockquotes are inspired by emails and use ">" characters. Simply add a > before each
line.
	> BLOCKQUOTE
	>
	> WITH TWO PARAGRAPHS
Blockquotes can also be nested.
	> BLOCKQUOTE
	> > NESTED BLOCKQUOTE
	> BLOCKQUOTE
Blockquotes can contain Markdown syntax
	> ##BLOCKQUOTE H2 HEADER
	>
	> 1. BLOCKQUOTE LIST ITEM 1
	> 2. BLOCKQUOTE LIST ITEM 2
	> BLOCKQUOTE

###LISTS
Unordered lists are created by adding asterisks, plus/minus signs or hyphens to the beginning of 
each line.
	Unordered list:
	- Item 1
	- Item 2
Ordered lists are the same but with numbers followed by a period.
	Ordered list:
	1. Item 1
	2. Item 2
Note that Markdown recognizes them as a part of a syntax for creating lists, not as numbers
themselves. Therefore it does not matter which numbers you use and the example below will result
in the same list as the example above. This is subject to change however.
	Ordered list:
	3. Item 1
	9. Item 2

###LINKS AND IMAGES
Hyperlinks are created by wrapping the desired text with brackets and the link right beside that
(without a separating space between), wrapped with parenteses.
	[MY HYPERLINK TO GOOGLE](www.google.com)
To insert an image; do the same but with an exclamation mark at the beginning of the link to the 
image.

##SYNTAX (contd.)
Less used, but useful Markdown syntax.

###LINE BREAKS

###CODE BLOCKS
