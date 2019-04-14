A fork of [GDA Corpus Browser](http://www.gsk.or.jp/catalog/gsk2011-a/).

GDA Corpus Browser is a tool made by [Gengo-Shigen-Kyokai (GSK)](http://www.gsk.or.jp/en/) for browsing the following two Japanese corpora.

+ [Annotated Corpus of Iwanami Japanese Dictionary Fifth Edition 2004](http://www.gsk.or.jp/en/catalog/gsk2010-a/)
+ [News article GDA Corpus 2004](http://www.gsk.or.jp/en/catalog/gsk2009-b/)

The tool is publicly available under Apache License, Version 2.0.

## Usage
```bash
# Linuxbrew / Homebrew
brew install openjdk sbt
sbt run
```
When characters appear as white squares, add font files to `lib/fonts/fallback` under the root directory of the JRE you are using.
You can find it by `readlink -f $(which java)`.
Suppose it shows `/home/linuxbrew/.linuxbrew/Cellar/openjdk@11/11+28/bin/java`.
Then, create symbolic links to font files as below.
```
cd /home/linuxbrew/.linuxbrew/Cellar/openjdk@11/11+28/lib
mkdir -p fonts/fallback
ln -s SOME_FONT_FILE fonts/fallback/
```
