#! /bin/sh
mysqladmin --user=root --password=pool create cfpool
mysqladmin --user=root --password=pool create nfl
mysqladmin --user=root --password=pool create cfnjl
mysqladmin --user=root --password=pool create nflnjl
mysqladmin --user=root --password=pool create cfhusker
mysqladmin --user=root --password=pool create nflhusker

mysql --user=root --password=pool mysql <<A
grant all privileges on cfpool.* to cfpool@localhost identified by 'pool';
A

mysql --user=root --password=pool mysql <<A
grant all privileges on nfl.* to nflpool@localhost identified by 'pool';
A

mysql --user=root --password=pool mysql <<A
grant all privileges on cfnjl.* to cfpool@localhost identified by 'pool';
A

mysql --user=root --password=pool mysql <<A
grant all privileges on nflnjl.* to nflpool@localhost identified by 'pool';
A

mysql --user=root --password=pool mysql <<A
grant all privileges on cfhusker.* to cfpool@localhost identified by 'pool';
A

mysql --user=root --password=pool mysql <<A
grant all privileges on nflhusker.* to nflpool@localhost identified by 'pool';
A
