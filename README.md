# TokensSystem
TokensSystem is a plugin for adding a new currencies.
Default TokenTypes: JobsToken and VoteToken

## Jobs
The plugin supports [JobsReborn](https://www.spigotmc.org/resources/jobs-reborn.4216/) and will automatically give a JobsToken to a player when reaching a new level.

## Vote
The plugin supports [NuVotifier](https://www.spigotmc.org/resources/nuvotifier.13449/) and will automatically give out VoteTokens when a player votes.

## BossShopPro
The plugin adds tokens as a PriceType to [BossShopPro](https://www.spigotmc.org/resources/bossshoppro-the-most-powerful-chest-gui-shop-menu-plugin.222/)
The name of the PriceType will follow format: TokenTypeNameToken, e.g. voteToken and jobsToken

## PlaceholderAPI
The plugin will automatically create placeholders for [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) to use.
### Format
%tokenssystem_TOKENTYPE% - Amount of tokens of specific TokenType a player has.

# Development
## Testing
1. Setup test database (mysql)
2. Copy main/resources/config.yml to test/config.yml
3. Enter database information in config.yml
4. Run tests!