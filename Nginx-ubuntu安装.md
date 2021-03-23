## 1.Nginx文件位置：

```text
/usr/sbin/nginx：主程序

/etc/nginx：存放配置文件

/usr/share/nginx：存放静态文件

/var/log/nginx：存放日志
```

 

## 2.安装gcc g++的依赖库

```shell
sudo apt-get install build-essential
sudo apt-get install libtool
```

 

## 3.安装pcre依赖库（http://www.pcre.org/）

```shell
sudo apt-get update
sudo apt-get install libpcre3 libpcre3-dev
```

 

## 4.安装zlib依赖库（[http://www.zlib.net](http://www.zlib.net/)）

```shell
sudo apt-get install zlib1g-dev
```

## 5.安装SSL依赖库（16.04默认已经安装了）

```shell
sudo apt-get install openssl
```

## 6.安装Nginx

```shell
#下载最新版本：
wget http://nginx.org/download/nginx-1.13.6.tar.gz
#解压：
tar -zxvf nginx-1.13.6.tar.gz
#进入解压目录：
cd nginx-1.13.6
#配置：
./configure --prefix=/usr/local/nginx 
#编译：
make
#安装：
sudo make install
#启动：
sudo /usr/local/nginx/sbin/nginx -c /usr/local/nginx/conf/nginx.conf
#注意：-c 指定配置文件的路径，不加的话，nginx会自动加载默认路径的配置文件，可以通过-h查看帮助命令。

#查看进程：
ps -ef | grep nginx
```

 

## 7.配置软链接

```shell
sudo ln -s /usr/local/nginx/sbin/nginx /usr/bin/nginx
```

 

## 8.配置开机启动服务

在/etc/init.d/下创建nginx文件，sudo vim /etc/init.d/nginx，内容如下：

```shell
#!/bin/sh
 
### BEGIN INIT INFO
# Provides:      nginx
# Required-Start:    $local_fs $remote_fs $network $syslog $named
# Required-Stop:     $local_fs $remote_fs $network $syslog $named
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: starts the nginx web server
# Description:       starts nginx using start-stop-daemon
### END INIT INFO
 
PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin
DAEMON=/usr/local/nginx/sbin/nginx
NAME=nginx
DESC=nginx
 
# Include nginx defaults if available
if [ -r /etc/default/nginx ]; then
    . /etc/default/nginx
fi
 
STOP_SCHEDULE="${STOP_SCHEDULE:-QUIT/5/TERM/5/KILL/5}"
 
test -x $DAEMON || exit 0
 
. /lib/init/vars.sh
. /lib/lsb/init-functions
 
# Try to extract nginx pidfile
PID=$(cat /usr/local/nginx/conf/nginx.conf | grep -Ev '^\s*#' | awk 'BEGIN { RS="[;{}]" } { if ($1 == "pid") print $2 }' | head -n1)
if [ -z "$PID" ]; then
    PID=/run/nginx.pid
fi
 
if [ -n "$ULIMIT" ]; then
    # Set ulimit if it is set in /etc/default/nginx
    ulimit $ULIMIT
fi
 
start_nginx() {
    # Start the daemon/service
    #
    # Returns:
    #   0 if daemon has been started
    #   1 if daemon was already running
    #   2 if daemon could not be started
    start-stop-daemon --start --quiet --pidfile $PID --exec $DAEMON --test > /dev/null \
        || return 1
    start-stop-daemon --start --quiet --pidfile $PID --exec $DAEMON -- \
        $DAEMON_OPTS 2>/dev/null \
        || return 2
}
 
test_config() {
    # Test the nginx configuration
    $DAEMON -t $DAEMON_OPTS >/dev/null 2>&1
}
 
stop_nginx() {
    # Stops the daemon/service
    #
    # Return
    #   0 if daemon has been stopped
    #   1 if daemon was already stopped
    #   2 if daemon could not be stopped
    #   other if a failure occurred
    start-stop-daemon --stop --quiet --retry=$STOP_SCHEDULE --pidfile $PID --name $NAME
    RETVAL="$?"
    sleep 1
    return "$RETVAL"
}
 
reload_nginx() {
    # Function that sends a SIGHUP to the daemon/service
    start-stop-daemon --stop --signal HUP --quiet --pidfile $PID --name $NAME
    return 0
}
 
rotate_logs() {
    # Rotate log files
    start-stop-daemon --stop --signal USR1 --quiet --pidfile $PID --name $NAME
    return 0
}
 
upgrade_nginx() {
    # Online upgrade nginx executable
    # http://nginx.org/en/docs/control.html
    #
    # Return
    #   0 if nginx has been successfully upgraded
    #   1 if nginx is not running
    #   2 if the pid files were not created on time
    #   3 if the old master could not be killed
    if start-stop-daemon --stop --signal USR2 --quiet --pidfile $PID --name $NAME; then
        # Wait for both old and new master to write their pid file
        while [ ! -s "${PID}.oldbin" ] || [ ! -s "${PID}" ]; do
            cnt=`expr $cnt + 1`
            if [ $cnt -gt 10 ]; then
                return 2
            fi
            sleep 1
        done
        # Everything is ready, gracefully stop the old master
        if start-stop-daemon --stop --signal QUIT --quiet --pidfile "${PID}.oldbin" --name $NAME; then
            return 0
        else
            return 3
        fi
    else
        return 1
    fi
}
 
case "$1" in
    start)
        log_daemon_msg "Starting $DESC" "$NAME"
        start_nginx
        case "$?" in
            0|1) log_end_msg 0 ;;
            2)   log_end_msg 1 ;;
        esac
        ;;
    stop)
        log_daemon_msg "Stopping $DESC" "$NAME"
        stop_nginx
        case "$?" in
            0|1) log_end_msg 0 ;;
            2)   log_end_msg 1 ;;
        esac
        ;;
    restart)
        log_daemon_msg "Restarting $DESC" "$NAME"
 
        # Check configuration before stopping nginx
        if ! test_config; then
            log_end_msg 1 # Configuration error
            exit $?
        fi
 
        stop_nginx
        case "$?" in
            0|1)
                start_nginx
                case "$?" in
                    0) log_end_msg 0 ;;
                    1) log_end_msg 1 ;; # Old process is still running
                    *) log_end_msg 1 ;; # Failed to start
                esac
                ;;
            *)
                # Failed to stop
                log_end_msg 1
                ;;
        esac
        ;;
    reload|force-reload)
        log_daemon_msg "Reloading $DESC configuration" "$NAME"
 
        # Check configuration before stopping nginx
        #
        # This is not entirely correct since the on-disk nginx binary
        # may differ from the in-memory one, but that's not common.
        # We prefer to check the configuration and return an error
        # to the administrator.
        if ! test_config; then
            log_end_msg 1 # Configuration error
            exit $?
        fi
 
        reload_nginx
        log_end_msg $?
        ;;
    configtest|testconfig)
        log_daemon_msg "Testing $DESC configuration"
        test_config
        log_end_msg $?
        ;;
    status)
        status_of_proc -p $PID "$DAEMON" "$NAME" && exit 0 || exit $?
        ;;
    upgrade)
        log_daemon_msg "Upgrading binary" "$NAME"
        upgrade_nginx
        log_end_msg $?
        ;;
    rotate)
        log_daemon_msg "Re-opening $DESC log files" "$NAME"
        rotate_logs
        log_end_msg $?
        ;;
    *)
        echo "Usage: $NAME {start|stop|restart|reload|force-reload|status|configtest|rotate|upgrade}" >&2
        exit 3
        ;;
esac
```

 

 

```shell
#设置服务脚本有执行权限
sudo chmod +x /etc/init.d/nginx
#注册服务
cd /etc/init.d/
sudo update-rc.d nginx defaults
```

 

现在基本上就可以开机启动了，常用的命令如下：

```shell
sudo service nginx {start|stop|restart|reload|force-reload|status|configtest|rotate|upgrade}
```

 

 

 

 

 
