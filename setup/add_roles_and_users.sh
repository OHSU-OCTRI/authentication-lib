#!/bin/bash

SELF_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

SQL="sql/add_roles_and_users.sql"

echo "
--------------------------------------------------------------------------------
 This script will prompt for various database properties.
 Once gathered it will import $SQL
 Default values are presented in square brackets. Hit enter to accept.
 Only the database name is required.
 You may run this script from any directory.
--------------------------------------------------------------------------------
"

read -p "Enter host [127.0.0.1]: " DB_HOST
[[ -z $DB_HOST ]] && DB_HOST="127.0.0.1"

read -p "Enter port [3306]: " DB_PORT
[[ -z $DB_PORT ]] && DB_PORT=3306

read -p "Enter user [root]: " DB_USER
[[ -z $DB_USER ]] && DB_USER="root"

read -p "Enter password [root]: " DB_PASS
[[ -z $DB_PASS ]] && DB_PASS="root"

read -p "Enter database (required): " DB
[[ -z $DB ]] && echo "You must enter a database name!" && exit

echo "*** Connecting to ${DB_HOST}:${DB_PORT}/${DB} ***"

echo "*** Importing $SQL ***"
mysql -u $DB_USER -p$DB_PASS -h $DB_HOST -P $DB_PORT $DB < $SELF_DIR/$SQL

echo "*** Done ***"
