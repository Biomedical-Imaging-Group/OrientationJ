"""Let the theme include a template kept in docs/.

The banner is content, so it lives beside the pages in docs/banner.html rather
than among the theme files. Jinja only searches the theme directories, so this
hook adds docs/ to its loader; mkdocs.yml excludes the file from the build so
that it is not also published as a page of its own.
"""
from jinja2 import ChoiceLoader, FileSystemLoader


def on_env(env, config, files):
    env.loader = ChoiceLoader([FileSystemLoader(config["docs_dir"]), env.loader])
    return env
