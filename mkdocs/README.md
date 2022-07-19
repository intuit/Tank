# tank-docs
This is the new manifestation of Tank's documentation, leveraging the very powerful docs solution "Material fo MkDocs"(mkdocs-material)

# What does this new doc site fulfill?

This new doc site, being built on mkdocs-material,

* Ships with a very user-friendly documentation ecosystem. 
* Ships with a  powerful Search feature to easily search any content within the docs.
* Provides a streamlined Navigation experience, being organised for the best understanding experience
* The documentation itself has been reviewed and revamped at places, for better understandability
* There are code blocks and content tabs, which keep the reader focused with its concise layout.
* Last but not the least, this doc site is version controlled on Git, which is a plus.

# Installation and ops
## Running on local 
Get into the `mkdocs` folder. 
Install dependencies from Requirements file:  `pip install -r requirements.txt`
Run locally using comamnd: `python3 -m mkdocs serve`

## Publishing the docs site
Publish docs site using command: `mkdocs gh-deploy --force`

## References
Detailed documentation for mkdocs-material is available under https://squidfunk.github.io/mkdocs-material/

# Credits
Made with ❤️ using `squidfunk/mkdocs-material`