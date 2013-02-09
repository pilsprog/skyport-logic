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


###QUOTE BLOCKS
###LISTS
###LINKS

##SYNTAX (contd.)
###LINE BREAKS
###CODE BLOCKS
