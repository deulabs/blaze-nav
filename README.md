blaze-nav
=============

**blaze-nav** - blaze fast navigation plugin for IntelliJ and derivatives (IDEA, PHPStorm, AppCode, WebStorm, PyCharm, RubyMine) inspired by [ace-jump-mode](https://github.com/winterTTr/ace-jump-mode) for Emacs. In comparison with [similar plugin](https://github.com/johnlindquist/AceJump) **blaze-nav** stays more closely to Emacs version and is less visually noisy.

- [Contents](#table-of-contents-generator)
    - [Software requirements](#software-requirements)
    - [Installation](#installation)
    - [Hotkeys](#hotkeys)
    - [How it works](#how-it-works)
        - [Navigate to word](#navigate-to-word)
        - [Navigate to symbol](#navigate-to-symbol)
        - [Navigate to line](#navigate-to-line)
    - [Reporting bugs](#reporting-bugs)

## Software requirements

At the moment blaze-nav is compatible with latest versions of JetBrains IDEs (IntelliJ IDEA 12 / 13 EAP, PHPStorm 6, AppCode 2.1, etc).

## Installation

Download plugin jar [file](https://github.com/Bohtvaroh/blaze-nav/raw/master/blaze-nav.jar) and save it locally. Next, open IntelliJ plugin settings page, press 'Install plugin from disk…' button and select the file downloaded. Restart IDE.

## Hotkeys

1. **ctrl-;** - navigate to a word start;
2. **ctrl-alt-;** - navigate to any letter or symbol;
3. **ctrl-'** - navigate to a line.

These can be reassigned in settings - enter 'blaze nav' in filter on keymap preferences page to quickly locate them.

## How it works

![Initial screen](http://img94.imageshack.us/img94/1388/3g3u.png)

### Navigate to word

Having some text opened in editor, issue **ctrl-;** and enter letter or number which starts a word. Matching characters will be highlighted:

![Blaze fast navigate to word start](http://img35.imageshack.us/img35/7694/ifaf.png)

Next, enter one of the highlighted letters to move cursor to its position:

![Blaze fast navigate to word start continued](http://img341.imageshack.us/img341/2211/r1vo.png)

This way you can quickly navigate to any words on the screen with at most three times key press. When only single candidate exists, you'll be moved immediately.

### Navigate to symbol

To quickly navigate to any character on the screen, issue **ctrl-alt-;** and enter the symbol you need to navigate to:

![Blaze fast navigate to symbol start](http://img811.imageshack.us/img811/3520/aww7.png)

Similarly, enter one of the highlighted letters to move cursor to its position:

![Blaze fast navigate to symbol continued](http://img713.imageshack.us/img713/6638/mdgz.png)

### Navigate to line

To navigate to some visible line start, issue **ctrl-'**. Shortcuts for each line appear:

![Blaze fast navigate to line start](http://img221.imageshack.us/img221/4341/j3a.png)

Select one you need to move cursor to:

![Blaze fast navigate to line continued](http://img543.imageshack.us/img543/2660/l1nq.png)

## Reporting bugs

Use **Github** [issues](https://github.com/Bohtvaroh/blaze-nav/issues) page to report problems, suggest ideas and request new features.
