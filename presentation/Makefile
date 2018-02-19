all: slides.tex

slides.tex: slides.md
	@pandoc \
		-t beamer $< \
		--standalone \
		--slide-level 1 \
		--pdf-engine xelatex \
		-o $@

watch_md: slides.md
	@fswatch -o slides.md | xargs -n1 -I{} 'make'

watch_tex: slides.tex
	@latexmk -pdf -pvc slides.tex
