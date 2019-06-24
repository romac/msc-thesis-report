.PHONY: all clean

MAIN   = report.tex
OUTPUT = report.pdf
DEPS   = $(MAIN) lstscala.sty report.bib res/epfl-logo.pdf

all: $(OUTPUT)

$(OUTPUT): $(DEPS)
	tectonic $(MAIN)

clean:
	$(RM) $(OUTPUT)

