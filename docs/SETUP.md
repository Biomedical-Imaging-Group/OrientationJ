# Getting the site live — checklist

## 1. Copy these files into your repo

Drop the contents of this folder into the root of `OrientationJ`, keeping the layout:

```
OrientationJ/
├─ mkdocs.yml
├─ requirements-docs.txt
├─ .gitignore
├─ .github/workflows/docs.yml
└─ docs/
   ├─ index.md
   ├─ installation.md
   ├─ modes.md
   ├─ theory.md
   ├─ test-images.md
   ├─ benchmarking.md
   ├─ references.md
   └─ javascripts/mathjax.js
```

## 2. Edit three lines

In `mkdocs.yml`, replace `YOUR-USERNAME` with your GitHub account name in `site_url`,
`repo_url` and `repo_name`. Nothing else needs changing.

If your default branch is `master` rather than `main`, change it in
`.github/workflows/docs.yml` too.

## 3. Turn on Pages

Repository **Settings ▸ Pages ▸ Build and deployment ▸ Source** → select **GitHub Actions**.

This is the step people miss. The default is "Deploy from a branch", which ignores the
workflow and publishes your raw Markdown instead of the built site.

The repository must be public for GitHub Pages on a free account.

## 4. Push

```bash
git add .
git commit -m "Add documentation site"
git push
```

Watch the **Actions** tab. The first run takes two to three minutes. When it goes green the
site is at:

```
https://YOUR-USERNAME.github.io/OrientationJ/
```

## Editing afterwards

Click the pencil icon on any page — it opens that Markdown file in the GitHub web editor.
Commit, and the site rebuilds itself. No local setup needed.

## Optional: preview locally

```bash
pip install -r requirements-docs.txt
mkdocs serve      # http://127.0.0.1:8000
```

## When the custom domain arrives

1. Add a file named `CNAME` at the repository root containing just the domain.
2. Point the DNS at GitHub Pages (a `CNAME` record to `YOUR-USERNAME.github.io`, or the four
   `A` records if you want the apex domain).
3. Change `site_url` in `mkdocs.yml` to the new address.
4. **Settings ▸ Pages** → tick *Enforce HTTPS* once the certificate is issued.

From that point the repository can be transferred between accounts without the public URL
ever changing.

## Notes

- `mkdocs build --strict` in the workflow fails the build on broken internal links, so a bad
  link stops the deploy instead of shipping.
- `requirements-docs.txt` pins `mkdocs<2` on purpose. MkDocs 2.0 removes the plugin system
  with no migration path; an unpinned build would break without warning.
- Every page carries `TODO` comments marking what still needs writing. They are HTML
  comments, so they do not appear on the rendered site.
