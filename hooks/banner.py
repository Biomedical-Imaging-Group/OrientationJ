"""Put docs/banner.html at the top of the home page.

The banner is content, so it lives beside the pages rather than among theme
files, and it is plain HTML — no theme override, no Jinja. This hook reads it
once and prepends it to the rendered home page; mkdocs.yml excludes the file
from the build so that it is not also published as a page of its own.
"""
from pathlib import Path

_banner = None


def on_config(config):
    global _banner
    _banner = Path(config["docs_dir"], "banner.html").read_text(encoding="utf-8")
    return config


def on_page_content(html, page, config, files):
    if page.is_homepage:
        return _banner + html
    return html
