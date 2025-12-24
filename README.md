# fase4-order-service

```
name: Flyway Migration
on:
  push:
    branches: [ main ]
  jobs:
  migrate:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Code
        uses: actions/checkout@v3
      - name: Set up Java (Flyway needs it)
        uses: actions/setup-java@v3
        with:
          distribution: 'temurin'
          java-version: '21'
      - name: Install Flyway
        run: |
          curl -L https://download.red-gate.com/maven/release/com/redgate/flyway/flyway-commandline/11.19.0/flyway-commandline-11.19.0-linux-x64.tar.gz | tar xvz
          sudo ln -s $PWD/flyway-11.19.0/flyway /usr/local/bin/flyway
      - name: Run Flyway Migrations
        env:
          FLYWAY_URL: ${{ secrets.DATABASE_URL }}
          FLYWAY_USER: ${{ secrets.DATABASE_USER }}
          FLYWAY_PASSWORD: ${{ secrets.DATABASE_PASSWORD }}
        run: |
          flyway -url=$FLYWAY_URL \
                 -user=$FLYWAY_USER \
                 -password=$FLYWAY_PASSWORD \
                 -locations=filesystem:src/main/resources/migrations \
                 migrate
```

```
wget -qO- https://download.red-gate.com/maven/release/com/redgate/flyway/flyway-commandline/11.19.0/flyway-commandline-11.19.0-linux-x64.tar.gz | tar -xvz && sudo ln -s `pwd`/flyway-11.19.0/flyway /usr/local/bin 
```
