# GitHub Pages Build Instructions

This branch contains the source files for the Carpet PvP documentation website, hosted on GitHub Pages.

## Local Development

To run the site locally:

1. **Install Ruby and Bundler**
   ```bash
   # On Windows, use RubyInstaller: https://rubyinstaller.org/
   # On macOS: brew install ruby
   # On Linux: sudo apt install ruby-dev
   ```

2. **Install dependencies**
   ```bash
   bundle install
   ```

3. **Run the site**
   ```bash
   bundle exec jekyll serve
   ```

4. **Open in browser**
   - Visit `http://localhost:4000/Carpet-PvP/`

## Site Structure

```
├── _config.yml          # Jekyll configuration
├── index.md             # Homepage
├── installation.md      # Installation guide
├── commands.md          # Commands documentation
├── rules.md             # Rules reference
├── features.md          # Features overview
├── scarpet.md           # Scarpet scripting
├── troubleshooting.md   # Help and FAQ
└── _includes/           # Reusable components
    └── _layouts/        # Page templates
```

## GitHub Pages Deployment

The site automatically builds and deploys when you:

1. Push changes to the `gh-pages` branch
2. GitHub Actions will build the Jekyll site
3. Site will be available at: `https://andrewctf.github.io/Carpet-PvP/`

## Theme

This site uses the [Just the Docs](https://just-the-docs.github.io/just-the-docs/) theme with:

- Dark color scheme
- Search functionality
- Responsive navigation
- Code syntax highlighting
- Mobile-friendly design

## Content Updates

To update documentation:

1. Switch to `gh-pages` branch
2. Edit the relevant `.md` files
3. Commit and push changes
4. Site will automatically rebuild

## Customization

The site can be customized through:

- `_config.yml` - Site settings and theme options
- Custom CSS in `assets/css/` 
- Custom layouts in `_layouts/`
- Additional pages as needed
