# Systems Modeling With Stainless

> January 2018

## Abstract

We present three orthogonal approaches to the verification of non-trivial programs, and systems of medium complexity, such as evaluators for domain specific languages, small actor systems, and implementations of bi-party communication protocols. We first discuss the design, implementation and semantics of a partial symbolic evaluation procedure for PureScala programs, as well as the challenges we faced to ensure termination of said procedure. Next, we define a model for actor systems which we implement as a library for Stainless, and explain how to verify that a global invariant of an actor system is preserved between each step of execution, with supporting examples. At last, we show how the addition of linear types to the type system of Stainless allows us to safely model bi-party communication protocols expressed as session types in PureScala, with a supporting example.

## Report

[The report is available in PDF](report/report.pdf)

## Presentation

[The slides from the presentation are available in PDF](presentation/slides.pdf)

