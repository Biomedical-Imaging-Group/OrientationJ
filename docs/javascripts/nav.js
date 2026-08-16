/* Menu entries that point inside a page.
 *
 * MkDocs refuses "page.md#section" in the nav — it reports a missing file and
 * writes the string out as a broken link. Entries are therefore written with a
 * scheme of our own, which MkDocs passes through untouched:
 *
 *     - The structure tensor: "oj:theory/#the-structure-tensor"
 *
 * This turns them into real links, relative to the site root, and gives them
 * the same red highlight as a page entry while their section is on screen —
 * so a section of a page and a page of its own behave alike in the menu.
 */
(function () {
  'use strict';

  function siteRoot() {
    // the logo always links to the root of the site, from wherever we are
    var logo = document.querySelector('.md-header__button.md-logo');
    var href = logo ? logo.getAttribute('href') : '.';
    return href.replace(/\/?$/, '/');
  }

  function resolve() {
    var root = siteRoot();
    var links = [];
    document.querySelectorAll('a[href^="oj:"]').forEach(function (a) {
      a.setAttribute('href', root + a.getAttribute('href').slice(3));
      links.push(a);
    });
    return links;
  }

  function track(links) {
    // the entries whose target lives on the page being read
    var here = links.filter(function (a) {
      var url = new URL(a.href, location.href);
      return url.pathname === location.pathname && url.hash;
    });
    if (!here.length) return;

    var sections = here.map(function (a) {
      return { link: a, el: document.getElementById(decodeURIComponent(a.hash.slice(1))) };
    }).filter(function (s) { return s.el; });

    var mark = function (link) {
      here.forEach(function (a) { a.classList.remove('md-nav__link--active'); });
      if (link) link.classList.add('md-nav__link--active');
    };

    var onScroll = function () {
      var current = null;
      sections.forEach(function (s) {
        if (s.el.getBoundingClientRect().top <= 120) current = s.link;
      });
      mark(current || sections[0].link);
    };
    addEventListener('scroll', onScroll, { passive: true });
    onScroll();
  }

  // Home, between the previous and next links of the footer bar
  function footerHome() {
    var bar = document.querySelector('.md-footer__inner');
    if (!bar || bar.querySelector('.md-footer__home')) return;
    var next = bar.querySelector('.md-footer__link--next');
    var home = document.createElement('a');
    home.className = 'md-footer__home';
    home.href = siteRoot();
    home.textContent = 'Home';
    bar.insertBefore(home, next || null);
  }

  // anything leaving the documentation opens in a tab of its own
  function externalLinks() {
    document.querySelectorAll('a[href]').forEach(function (a) {
      var href = a.getAttribute('href');
      if (!/^(https?:)?\/\//.test(href)) return;         // relative: stays here
      if (a.hostname === location.hostname) return;      // the site itself
      a.setAttribute('target', '_blank');
      a.setAttribute('rel', 'noopener');
    });
  }

  var start = function () { track(resolve()); footerHome(); externalLinks(); };
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', start);
  } else {
    start();
  }
})();
