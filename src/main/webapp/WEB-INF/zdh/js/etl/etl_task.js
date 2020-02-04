
(function(document, window, $) {

  // Example Bootstrap Table Events
  // ------------------------------
  (function() {

      $('#add').click(function () {
          parent.layer.open({
              type: 2,
              title: 'ETL任务配置',
              shadeClose: false,
              resize: true,
              fixed: false,
              maxmin: true,
              shade: 0.1,
              area : ['45%', '60%'],
              //area: ['450px', '500px'],
              content: "etl_task_add_index?id=-1", //iframe的url
              end:function () {
                  $('#exampleTableEvents').bootstrapTable('refresh', {
                      url : 'etl_task_list'
                  });
              }
          });
      })

      $('#remove').click(function () {

        var rows = $("#exampleTableEvents").bootstrapTable('getSelections');// 获得要删除的数据
        if (rows.length == 0) {// rows 主要是为了判断是否选中，下面的else内容才是主要
            alert("请先选择要删除的记录!");
            return;
        } else {
            var ids = new Array();// 声明一个数组
            $(rows).each(function() {// 通过获得别选中的来进行遍历
                ids.push(this.id);// cid为获得到的整条数据中的一列
            });
            console.log(ids)
            deleteMs(ids)
        }

    })

      function deleteMs(ids) {
          $.ajax({
              url : "etl_task_delete",
              data : "ids=" + ids,
              type : "post",
              dataType : "json",
              success : function(data) {
                  console.info("success")
                  $('#exampleTableEvents').bootstrapTable('refresh', {
                      url : 'etl_task_list'
                  });
              },
              error: function (data) {
                  console.info("error: " + data.responseText);
              }

          });
      }

      window.operateEvents = {
          'click #edit': function (e, value, row, index) {

              $("#id").val(row.id)


              top.layer.open({
                  type: 2,
                  title: 'ETL任务配置',
                  shadeClose: false,
                  resize: true,
                  fixed: false,
                  maxmin: true,
                  shade: 0.1,
                  area : ['45%', '60%'],
                  //area: ['450px', '500px'],
                  content: "etl_task_add_index?id="+row.id, //iframe的url
                  end:function () {
                      $('#exampleTableEvents').bootstrapTable('refresh', {
                          url : 'etl_task_list'
                      });
                  }
              });

          },
          'click #del': function (e, value, row, index) {
              var ids = new Array();// 声明一个数组
              ids.push(row.id)
              deleteMs(ids)
          }
      };

      function operateFormatter(value, row, index) {
          return [
              ' <div class="btn-group hidden-xs" id="exampleTableEventsToolbar" role="group">' +
              ' <button id="edit" name="edit" type="button" class="btn btn-outline btn-sm"><i class="glyphicon glyphicon-edit" aria-hidden="true"></i>\n' +
              '                                    </button>',
              ' <button id="del" name="del" type="button" class="btn btn-outline btn-sm">\n' +
              '                                        <i class="glyphicon glyphicon-trash" aria-hidden="true"></i>\n' +
              '                                    </button>'
               +
              '</div>'

          ].join('');

      }


    $('#exampleTableEvents').bootstrapTable({
      url: "etl_task_list",
      search: true,
      pagination: true,
      showRefresh: true,
      showToggle: true,
      showColumns: true,
      iconSize: 'outline',
      toolbar: '#exampleTableEventsToolbar',
      icons: {
        refresh: 'glyphicon-repeat',
        toggle: 'glyphicon-list-alt',
        columns: 'glyphicon-list'
      },
        columns: [{
            checkbox: true,
            field:'state',
            sortable:true
        }, {
            field: 'id',
            title: 'ID',
            sortable:false
        }, {
            field: 'etl_context',
            title: 'etl说明',
            sortable:false
        },{
            field: 'data_sources_choose_input',
            title: '输入数据源ID',
            sortable:false
        }, {
            field: 'data_source_type_input',
            title: '输入数据源类型',
            sortable:false
        }, {
            field: 'data_sources_table_name_input',
            title: '输入数据源表',
            sortable:false
        }, {
          field: 'data_sources_table_columns',
          title: '输入数据源表字段',
          sortable:false
      }, {
            field: 'data_sources_file_name_input',
            title: '输入数据源文件',
            sortable:false
        }, {
            field: 'data_sources_file_columns',
            title: '输入数据源文件SCHEMA',
            sortable:false
        }, {
          field: 'data_sources_params_input',
          title: '输入数据源参数',
          sortable:false
      }, {
            field: 'data_sources_filter_input',
            title: '输入数据源过滤条件',
            sortable:false
        },{
          field: 'data_sources_choose_output',
          title: '输出数据源ID',
          sortable:false
      },{
          field: 'data_source_type_output',
          title: '输出数据源类型',
          sortable:true
      },{
          field: 'data_sources_table_name_output',
          title: '输出数据源表',
          sortable:false
      },{
          field: 'data_sources_file_name_output',
          title: '输出数据源文件',
          sortable:false
      },{
          field: 'data_sources_params_output',
          title: '输出数据源参数',
          sortable:false
      },{
            field: 'data_sources_clear_output',
            title: '输出数据删除条件',
            sortable:false
        },{
          field: 'column_datas',
          title: '输入输出字段映射',
          sortable:false
      },{
            field: 'operate',
            title: '操作',
            events: operateEvents,//给按钮注册事件
            width:90,
            formatter: operateFormatter //表格中增加按钮
        }]
    });

    var $result = $('#examplebtTableEventsResult');
  })();
})(document, window, jQuery);
