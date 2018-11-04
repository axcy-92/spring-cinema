/**
 *
 * @author by Aleksei_Cherniavskii
 */

$(document).ready(function () {
    $('#edit').click(function () {
        $('.user-field').removeAttr('readonly');
        $('.only-edit-mode').removeClass('hidden');
        $('.edit-mode').removeClass('hidden');
        $('.view-mode').addClass('hidden');
    });

    $('#cancel').click(function () {
        location.reload();
    });

    $(document).on('click', '.btn-add', function(e) {
        e.preventDefault();

        var formGroup = $('.form-group.roles:first'),
            roleGroup = $(this).parents('.role-group:first'),
            newRoleGroup = $(roleGroup.clone()).appendTo(formGroup),
            nextIndex = $('.role-group').size();

        newRoleGroup.find('label.col-md-3.control-label').attr('for', 'role_' + nextIndex)
                                                         .text('Role ' + nextIndex + ":");
        var roleList = $(newRoleGroup.find('select.form-control').attr('id','role_' + nextIndex));

        if ($(this).hasClass('btn-default-role')) {
            newRoleGroup.removeClass('default-role');
            roleList.addClass('user-field').prop("disabled", false);
            roleList.attr('name', 'role');
            newRoleGroup.find('.btn-default-role').removeClass('btn-default-role');
        }

        $(this).removeClass('btn-add').addClass('btn-remove')
            .removeClass('btn-success').addClass('btn-danger')
            .html('<span class="glyphicon glyphicon-minus"></span>');

    }).on('click', '.btn-remove', function(e) {
        $(this).parents('.role-group:first').next('.role-group:first').remove();

        if ($('.btn-add').size() === 0) {
            $('.btn-remove:last').removeClass('btn-remove').addClass('btn-add')
                .removeClass('btn-danger').addClass('btn-success')
                .html('<span class="glyphicon glyphicon-plus"></span>');
        }

        e.preventDefault();
        return false;
    });

});