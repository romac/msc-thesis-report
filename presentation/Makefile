all: slides.tex

slides.tex: slides.md
	@pandoc \
		-t beamer $< \
		--standalone \
		--biblatex \
		--slide-level 2 \
		--pdf-engine xelatex \
		-o $@
	@echo 'Compiled $< to $@'

watch_md: slides.md
	@fswatch -o slides.md | xargs -n1 -I{} 'make'

watch_tex: slides.tex
	@latexmk -pdf -verbose -bibtex -file-line-error -synctex=1 --interaction=nonstopmode -pvc slides.tex
