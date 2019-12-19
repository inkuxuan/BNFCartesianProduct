# Simple BNF Defined Plain Text's Cartesian Production Generator

[中文(简体)](README.md)

## Grammar

* bracket including `'(', ')', '[', ']', '<', '>'` with`()` and `<>`'s meaning identical, `[]` meaning optional
* the OR operator `|`

e.g. input

```$xslt
I <wanna|want to|need to> have <lunch|dinner>.
```

output

```$xslt
I wanna have lunch.
I wanna have dinner.
I want to have lunch.
I want to have dinner.
I need to have lunch.
I need to have dinner.
```

## Usage

invoke class Main, parameters are as follows

`[-o <output-file>] [-append] <input-file> [input-file]...`

### parameters
* `-o` specify an output file, the default one is `output.txt`
* `-append` append to the output file rather than overwriting it
* multiple input file can be provided at once

Note: output encoding depends on the encoding of the first input file

# Dependencies

depend on `com.mozilla.universalchardet` to detect file encoding

PLEASE TELL ME IF THERE IS A BETTER ONE TO USE

[URL](https://code.google.com/archive/p/juniversalchardet/)

# LICENSE

com.mozilla.universalchardet uses [Mozilla Public License](https://www.mozilla.org/en-US/MPL/)

The rest of this project uses [WTFPL](http://www.wtfpl.net/txt/copying)